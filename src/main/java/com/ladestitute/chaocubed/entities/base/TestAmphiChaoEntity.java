package com.ladestitute.chaocubed.entities.base;

import com.google.common.collect.ImmutableList;
import com.ladestitute.chaocubed.ChaoCubedConfig;
import com.ladestitute.chaocubed.ChaoCubedMain;
import com.ladestitute.chaocubed.util.ChaoVariant;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public abstract class TestAmphiChaoEntity extends TamableAnimal {
    protected static final ImmutableList<? extends SensorType<? extends Sensor<? super TestAmphiChaoEntity>>> SENSOR_TYPES = ImmutableList.of(
            SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_ADULT, SensorType.HURT_BY, SensorType.AXOLOTL_ATTACKABLES, SensorType.AXOLOTL_TEMPTATIONS
    );
    protected static final ImmutableList<? extends MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
            MemoryModuleType.NEAREST_LIVING_ENTITIES,
            MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
            MemoryModuleType.NEAREST_VISIBLE_PLAYER,
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.PATH,
            MemoryModuleType.ATTACK_TARGET,
            MemoryModuleType.ATTACK_COOLING_DOWN,
            MemoryModuleType.NEAREST_ATTACKABLE,
            MemoryModuleType.TEMPTING_PLAYER,
            MemoryModuleType.TEMPTATION_COOLDOWN_TICKS,
            MemoryModuleType.IS_TEMPTED,
            MemoryModuleType.IS_PANICKING
    );

    private ChaoVariant variant;
    public int run_points;
    public int swim_points;
    public boolean shiny;

    // Note: the damage threshold for a chao's happiness reduction
    // should be its max health divided by 2.12 (rounded down)
    // Still haven't decided how much Stamina will contribute to health

    // Default health and happiness reduction damage threshold for all Chao
    // save for adults/second-stage evos, chao with high Stamina, etc
    public static final float DEFAULT_HEALTH = 18F;
    private final float HAPPINESS_REDUCTION_DAMAGE_THRESHOLD = Math.round((float) (DEFAULT_HEALTH/2.12));

    protected TestAmphiChaoEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setTame(false);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.moveControl = new TestAmphiChaoEntity.ChaoMoveControl(this);
        this.lookControl = new TestAmphiChaoEntity.ChaoLookControl(this, 20);
        this.setMaxUpStep(1.0F);
        this.variant = ChaoVariant.NORMAL;
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return null;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        // Add custom goals here if needed
    }

    @Override
    public void baseTick() {
        super.baseTick();
        int airSupply = this.getAirSupply();
        this.handleAirSupply(airSupply);
    }

    //Brain code starts here
    @Override
    protected Brain.Provider<TestAmphiChaoEntity> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> pDynamic) {
        return ChaoAi.makeBrain(this.brainProvider().makeBrain(pDynamic));
    }

    @Override
    public Brain<TestAmphiChaoEntity> getBrain() {
        return (Brain<TestAmphiChaoEntity>)super.getBrain();
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPackets.sendEntityBrain(this);
    }

    @Override
    protected void customServerAiStep() {
        this.level().getProfiler().push("chaoBrain");
        this.getBrain().tick((ServerLevel)this.level(), this);
        this.level().getProfiler().pop();
        this.level().getProfiler().push("chaoActivityUpdate");
        ChaoAi.updateActivity(this);
        this.level().getProfiler().pop();
    }

    //Brain code ends here

    //Pathfinding code starts here
    @Override
    public float getWalkTargetValue(BlockPos pPos, LevelReader pLevel) {
        return 0.0F;
    }

    @Override
    public void travel(Vec3 pTravelVector) {
        if (this.isInWater()) {
            // Swimming behavior
//            if (this.isControlledByLocalInstance()) {
//                this.moveRelative(this.getSpeed(), pTravelVector);
//                this.move(MoverType.SELF, this.getDeltaMovement());
//                this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
//            } else {
//                super.travel(pTravelVector);
//            }
            if (this.getRandom().nextFloat() >= 0.2F) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.02D, 0.0D)); // Buoyancy for floating
                super.travel(pTravelVector);
            }
            this.moveRelative(0.1F, pTravelVector); // Swim
            super.travel(pTravelVector);
        } else {
            // Behavior when on land
            super.travel(pTravelVector);
        }
    }

    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        return new AmphibiousPathNavigation(this, pLevel);
    }

    static class ChaoMoveControl extends SmoothSwimmingMoveControl {
        private final TestAmphiChaoEntity chao;

        public ChaoMoveControl(TestAmphiChaoEntity pChao) {
            super(pChao, 85, 10, 1F, 1F, false);
            this.chao = pChao;
        }

        @Override
        public void tick() {
            super.tick();
        }
    }
    //Pathfinding code starts here

    /**
     * Plays living's sound at its position
     */
    @Override
    public void playAmbientSound() {
            super.playAmbientSound();

    }

    // Variant methods
    public ChaoVariant getVariant() {
        return this.variant;
    }

    public void setVariant(ChaoVariant variant) {
        this.variant = variant;
    }

    // Implement taming logic here if needed
    @Override
    public boolean canBeLeashed(Player pPlayer) {
        return !this.isLeashed();
    }

    @Override
    public void setTame(boolean pTamed) {
        super.setTame(pTamed);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(DEFAULT_HEALTH);
    }

    //WIP flying and climbing
    //WIP flying and climbing
    //WIP flying and climbing
    //todo: still not sure how to implement this?
//    static class FlyingGoal extends Goal {
//        private final NeutralChaoEntity entity;
//
//        public FlyingGoal(NeutralChaoEntity entity) {
//            this.entity = entity;
//        }
//
//        @Override
//        public boolean canUse() {
//            return this.entity.getFlyPoints() >= 100;
//        }
//
//        @Override
//        public void tick() {
//            // Implement flying logic here
//        }
//    }

    //todo: implement, borrow from spider?
//    static class ClimbGoal extends Goal {
//        private final NeutralChaoEntity entity;
//
//        public ClimbGoal(NeutralChaoEntity entity) {
//            this.entity = entity;
//        }
//
//        @Override
//        public boolean canUse() {
//            return this.entity.horizontalCollision && this.entity.getPowerPoints() >= 200;
//        }
//
//        @Override
//        public void tick() {
//            // Implement climbing logic here
//        }
//    }
    //WIP flying and climbing
    //WIP flying and climbing
    //WIP flying and climbing

    //Air supply and breathing AI related code starts here
    protected void handleAirSupply(int airSupply) {
        if (this.isEyeInFluid(FluidTags.WATER)
                && !this.level().getBlockState(BlockPos.containing(this.getX(), this.getEyeY(), this.getZ())).is(Blocks.BUBBLE_COLUMN)) {
            boolean canDrown = !this.canBreatheUnderwater() && !MobEffectUtil.hasWaterBreathing(this);
            if (canDrown) {
                this.setAirSupply(this.decreaseAirSupply(this.getAirSupply()));
                if (this.getAirSupply() == -20)
                {
                    this.setAirSupply(0);
                    Vec3 vec3 = this.getDeltaMovement();

                    for (int i = 0; i < 8; ++i) {
                        double d2 = this.random.nextDouble() - this.random.nextDouble();
                        double d3 = this.random.nextDouble() - this.random.nextDouble();
                        double d4 = this.random.nextDouble() - this.random.nextDouble();
                        this.level().addParticle(ParticleTypes.BUBBLE, this.getX() + d2, this.getY() + d3, this.getZ() + d4, vec3.x, vec3.y, vec3.z);
                    }

                    this.hurt(this.damageSources().drown(), 2.0F); // Custom drowning damage
                }
                if(this.getAirSupply() <= this.getMaxAirSupply() / 3) {
                    Vec3 motion = this.getDeltaMovement();
                    this.setDeltaMovement(motion.x, 0.4, motion.z);
                }
            }

            if (!this.level().isClientSide && this.isPassenger() && this.getVehicle() != null && this.getVehicle().dismountsUnderwater()) {
                this.stopRiding();
            }
        } else if (this.getAirSupply() < this.getMaxAirSupply()) {
            this.setAirSupply(this.increaseAirSupply(this.getAirSupply()));
        }
    }

    @Override
    protected int decreaseAirSupply(int currentAir) {
        // Custom logic for decreasing air supply
        int i = EnchantmentHelper.getRespiration(this);
        return i > 0 && this.random.nextInt(i + 1) > 0 ? currentAir : currentAir - 1;
    }

    @Override
    protected int increaseAirSupply(int currentAir) {
        // Custom logic for increasing air supply, twice as fast on land
        return this.isInWaterOrBubble()
                ? Math.min(currentAir + 4, this.getMaxAirSupply())
                : Math.min(currentAir + 8, this.getMaxAirSupply());
    }

    @Override
    public int getMaxAirSupply() {
        return 2400; // Custom max air supply
    }
    //Air supply and breathing AI related code ends here

    @Override
    public boolean checkSpawnObstruction(LevelReader pLevel) {
        return pLevel.isUnobstructed(this);
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    public TagKey<Item> CHAO_FRUIT = ItemTags.create(new ResourceLocation(ChaoCubedMain.MODID, "chao_fruit"));

    @Override
    public boolean isFood(ItemStack pStack) {
        return pStack.is(CHAO_FRUIT);
    }

    @Override
    public boolean doHurtTarget(Entity pEntity) {
        boolean flag = pEntity.hurt(this.damageSources().mobAttack(this), (float)((int)this.getAttributeValue(Attributes.ATTACK_DAMAGE)));
        if (flag) {
            this.playSound(SoundEvents.AXOLOTL_ATTACK, 1.0F, 1.0F);
        }

        return flag;
    }

    /**
     * Called when the entity is attacked.
     */
    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        return super.hurt(pSource, pAmount);
    }

    @Override
    protected float getStandingEyeHeight(Pose pPose, EntityDimensions pDimensions) {
        return pDimensions.height * 1.453125F;
    }

    /**
     * The speed it takes to move the entity's head rotation through the faceEntity method.
     */
    @Override
    public int getMaxHeadXRot() {
        return 1;
    }

    @Override
    public int getMaxHeadYRot() {
        return 1;
    }

    @Override
    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.AXOLOTL_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.AXOLOTL_DEATH;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return this.isInWater() ? SoundEvents.AXOLOTL_IDLE_WATER : SoundEvents.AXOLOTL_IDLE_AIR;
    }

    @Override
    protected SoundEvent getSwimSplashSound() {
        return SoundEvents.AXOLOTL_SPLASH;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.AXOLOTL_SWIM;
    }

    @Override
    protected void usePlayerItem(Player pPlayer, InteractionHand pHand, ItemStack pStack) {
            super.usePlayerItem(pPlayer, pHand, pStack);
    }

    @Override
    public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
        return !this.isTame() | !this.hasCustomName();
    }

    class ChaoLookControl extends SmoothSwimmingLookControl {
        public ChaoLookControl(TestAmphiChaoEntity pChao, int pMaxYRotFromCenter) {
            super(pChao, pMaxYRotFromCenter);
        }

        /**
         * Updates look
         */
        @Override
        public void tick() {
                super.tick();
        }
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (this.isTame()) {
            if (this.isOwnedBy(pPlayer)) {
                if (this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
                    if (!this.level().isClientSide()) {
                        FoodProperties foodproperties = itemstack.getFoodProperties(this);
                        this.heal(foodproperties != null ? (float)foodproperties.getNutrition() : 1.0F);
                        this.usePlayerItem(pPlayer, pHand, itemstack);
                    }

                    return InteractionResult.sidedSuccess(this.level().isClientSide());
                }

                InteractionResult interactionresult = super.mobInteract(pPlayer, pHand);
                if (!interactionresult.consumesAction()) {
                    this.setOrderedToSit(!this.isOrderedToSit());
                    return InteractionResult.sidedSuccess(this.level().isClientSide());
                }

                return interactionresult;
            }
        } else if (this.isFood(itemstack)) {
            if (!this.level().isClientSide()) {
                this.usePlayerItem(pPlayer, pHand, itemstack);
                this.tryToTame(pPlayer);
                this.setPersistenceRequired();
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }

        InteractionResult interactionresult1 = super.mobInteract(pPlayer, pHand);
        if (interactionresult1.consumesAction()) {
            this.setPersistenceRequired();
        }

        return interactionresult1;
    }

    private void tryToTame(Player pPlayer) {
            this.tame(pPlayer);
            this.navigation.stop();
            this.setTarget(null);
            this.setOrderedToSit(true);
            this.level().broadcastEntityEvent(this, (byte)7);
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return ChaoCubedConfig.maxChaoGroupSize;
    }

}
