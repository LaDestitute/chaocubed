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
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public abstract class ChaoEntity extends TamableAnimal {
    protected static final ImmutableList<? extends SensorType<? extends Sensor<? super ChaoEntity>>> SENSOR_TYPES = ImmutableList.of(
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

    public static final EntityDataAccessor<Boolean> ORDERED_TO_SIT = SynchedEntityData.defineId(ChaoEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> RUN_POINTS = SynchedEntityData.defineId(ChaoEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SWIM_POINTS = SynchedEntityData.defineId(ChaoEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> FLY_POINTS = SynchedEntityData.defineId(ChaoEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> POWER_POINTS = SynchedEntityData.defineId(ChaoEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Boolean> SHINY = SynchedEntityData.defineId(ChaoEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> FLY_TIME = SynchedEntityData.defineId(ChaoEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(ChaoEntity.class, EntityDataSerializers.BOOLEAN);

    private ChaoVariant variant;
    private boolean dancingChao;
    @Nullable
    private BlockPos jukebox;

    // Note: the damage threshold for a chao's happiness reduction
    // should be its max health divided by 2.12 (rounded down)
    // Still haven't decided how much Stamina will contribute to health
    // Default health and happiness reduction damage threshold for all Chao
    // save for adults/second-stage evos, chao with high Stamina, etc
    public static final float DEFAULT_HEALTH = 18F;
    private final float HAPPINESS_REDUCTION_DAMAGE_THRESHOLD = Math.round((float) (DEFAULT_HEALTH/2.12));

    protected ChaoEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.moveControl = new ChaoEntity.ChaoMoveControl(this);
        this.lookControl = new ChaoEntity.ChaoLookControl(this, 20);
        this.variant = ChaoVariant.NORMAL;
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return null;
    }

    // Variant methods
    public ChaoVariant getVariant() {
        return this.variant;
    }

    public void setVariant(ChaoVariant variant) {
        this.variant = variant;
    }

    //Goal registration
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new ChaoAi.CustomSitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new ChaoAi.CustomFollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(3, new ChaoAi.ClimbGoal(this));
        this.goalSelector.addGoal(4, new ChaoAi.FlyGoal(this));


    }

    //Tick stuff
    @Override
    public void baseTick() {
        super.baseTick();
        int airSupply = this.getAirSupply();
        this.handleAirSupply(airSupply);
    }

    @Override
    public void aiStep() {
        if (this.jukebox == null || !this.jukebox.closerToCenterThan(this.position(), 3.46) || !this.level().getBlockState(this.jukebox).is(Blocks.JUKEBOX)) {
            this.dancingChao = false;
            this.jukebox = null;
        }

        super.aiStep();
    }

    @Override
    public void setRecordPlayingNearby(BlockPos pPos, boolean pIsPartying) {
        this.jukebox = pPos;
        this.dancingChao = pIsPartying;
    }

    public boolean isDancingChao() {
        return this.dancingChao;
    }

    //Synced data
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SWIM_POINTS, 0);
        this.entityData.define(RUN_POINTS, 0);
        this.entityData.define(POWER_POINTS, 0);
        this.entityData.define(FLY_POINTS, 0);
        this.entityData.define(ORDERED_TO_SIT, false);
        this.entityData.define(SHINY, false);
        this.entityData.define(FLY_TIME, 200);
        this.entityData.define(FLYING, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Run_Points", this.entityData.get(RUN_POINTS));
        pCompound.putInt("Swim_Points", this.entityData.get(SWIM_POINTS));
        pCompound.putInt("Fly_Points", this.entityData.get(FLY_POINTS));
        pCompound.putInt("Power_Points", this.entityData.get(POWER_POINTS));
        pCompound.putBoolean("Ordered_To_Sit", this.entityData.get(ORDERED_TO_SIT));
        pCompound.putBoolean("Shiny", this.entityData.get(SHINY));
        pCompound.putInt("FlyTime", this.getFlyTime());
        pCompound.putBoolean("Flying", this.entityData.get(FLYING));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.entityData.set(RUN_POINTS, pCompound.getInt("Run_Points"));
        this.entityData.set(SWIM_POINTS, pCompound.getInt("Swim_Points"));
        this.entityData.set(FLY_POINTS, pCompound.getInt("Fly_Points"));
        this.entityData.set(POWER_POINTS, pCompound.getInt("Power_Points"));
        this.entityData.set(ORDERED_TO_SIT, pCompound.getBoolean("Ordered_To_Sit"));
        this.entityData.set(SHINY, pCompound.getBoolean("Shiny"));
        this.setFlyTime(pCompound.getInt("FlyTime"));
        this.entityData.set(FLYING, pCompound.getBoolean("Flying"));
    }

    public int getFlyTime() {
        return this.entityData.get(FLY_TIME);
    }

    public int getFlyPoints() {
        return this.entityData.get(FLY_POINTS);
    }

    public int getPowerPoints() {
        return this.entityData.get(FLY_POINTS);
    }


    public void setFlyTime(int flyTime) {
        this.entityData.set(FLY_TIME, flyTime);
    }

    //Brain code starts here
    @Override
    protected Brain.Provider<ChaoEntity> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> pDynamic) {
        return ChaoAi.makeBrain(this.brainProvider().makeBrain(pDynamic));
    }

    @Override
    public Brain<ChaoEntity> getBrain() {
        return (Brain<ChaoEntity>)super.getBrain();
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
        if (this.isOrderedToSit()) {
            return;
        }
        if(this.isInWater()) {
            if (this.getRandom().nextFloat() >= 0.2F) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.02D, 0.0D)); // Buoyancy for floating
                super.travel(pTravelVector);
            }
            this.moveRelative(0.1F, pTravelVector);
        }
        super.travel(pTravelVector);
    }

    @Override
    protected PathNavigation createNavigation(Level pLevel) {
            return new AmphibiousPathNavigation(this, pLevel);
    }

    static class ChaoMoveControl extends SmoothSwimmingMoveControl {
        private final ChaoEntity chao;

        public ChaoMoveControl(ChaoEntity pChao) {
            super(pChao, 85, 10, 1F, 1F, false);
            this.chao = pChao;
        }

        @Override
        public void tick() {
                super.tick();
        }
    }

//    static class ChaoMoveControl extends SmoothSwimmingMoveControl {
//        private final ChaoEntity axolotl;
//
//        public ChaoMoveControl(ChaoEntity pAxolotl) {
//            super(pAxolotl, 85, 10, 1F, 1F, false);
//            this.axolotl = pAxolotl;
//        }
//
//        @Override
//        public void tick() {
//                super.tick();
//        }
//    }

    @Override
    public boolean checkSpawnObstruction(LevelReader pLevel) {
        return pLevel.isUnobstructed(this);
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }
    //Pathfinding code end here

    //Climbing code
    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.getEntityData().get(POWER_POINTS) >= 200) {
            this.setClimbing(this.horizontalCollision);
        }
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        if(this.getEntityData().get(FLY_POINTS) >= 200)
        {
            return false;
        }
        else return true;
    }

    @Override
    public boolean onClimbable() {
        return this.isClimbing();
    }

    public boolean isClimbing() {
        return (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
    }

    public void setClimbing(boolean pClimbing) {
        byte b0 = this.entityData.get(DATA_FLAGS_ID);
        if (pClimbing) {
            b0 = (byte)(b0 | 1);
        } else {
            b0 = (byte)(b0 & -2);
        }

        this.entityData.set(DATA_FLAGS_ID, b0);
    }
    //Climbing code

    //Flying code
    public boolean isFlying() {
        return !this.onGround();
    }
    //Flying code

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

    //Taming logic
    public TagKey<Item> CHAO_FRUIT = ItemTags.create(new ResourceLocation(ChaoCubedMain.MODID, "chao_fruit"));

    @Override
    public boolean isFood(ItemStack pStack) {
        return pStack.is(CHAO_FRUIT);
    }

    public boolean isOrderedToSit() {
        return this.entityData.get(ORDERED_TO_SIT);
    }

    public void setOrderedToSit(boolean sit) {
        this.entityData.set(ORDERED_TO_SIT, sit);
    }

    @Override
    public boolean canBeLeashed(Player pPlayer) {
        return !this.isLeashed();
    }

    @Override
    public void setTame(boolean pTamed) {
        super.setTame(pTamed);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(DEFAULT_HEALTH);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        // Check if the item is in the tag list
        boolean isHealingItem = itemstack.is(CHAO_FRUIT);
        boolean isHandEmpty = itemstack.isEmpty();

        if (isHandEmpty) {
            if (!this.level().isClientSide && this.isTame() && !this.isFlying()) {
                this.setOrderedToSit(!this.isOrderedToSit());
                this.jumping = false;
                this.navigation.stop();
                this.setTarget(null);
                return InteractionResult.SUCCESS;
            }
        } else if (isHealingItem) {
            if (!this.level().isClientSide && this.isTame()) {
                if (this.getHealth() < this.getMaxHealth()) {
                    this.heal((float)itemstack.getFoodProperties(this).getNutrition()); // Define the healing amount
                    itemstack.shrink(1); // Decrease the item count by 1
                    return InteractionResult.SUCCESS;
                }
            } else if (!this.level().isClientSide && !this.isTame()) {
                this.setTame(true);
                this.setOwnerUUID(player.getUUID());
                this.getNavigation().stop();
                this.setTarget(null);
                this.setOrderedToSit(true);
                this.level().broadcastEntityEvent(this, (byte) 7);
                return InteractionResult.SUCCESS;
            }
        }

        return super.mobInteract(player, hand);
    }
    //Taming logic

    //Damage stuff
    @Override
    public boolean doHurtTarget(Entity pEntity) {
        boolean flag = pEntity.hurt(this.damageSources().mobAttack(this), (float)((int)this.getAttributeValue(Attributes.ATTACK_DAMAGE)));
        if (flag) {
            this.playSound(SoundEvents.AXOLOTL_ATTACK, 1.0F, 1.0F);
        }

        return flag;
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        return super.hurt(pSource, pAmount);
    }
    // Damage stuff

    //Misc
    @Override
    protected float getStandingEyeHeight(Pose pPose, EntityDimensions pDimensions) {
        return pDimensions.height * 0.93F;
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
    public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
        return false;
    }

    class ChaoLookControl extends SmoothSwimmingLookControl {
        public ChaoLookControl(ChaoEntity pChao, int pMaxYRotFromCenter) {
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
    public int getMaxSpawnClusterSize() {
        return ChaoCubedConfig.maxChaoGroupSize;
    }
    //Misc

    //Sounds
    @Override
    public void playAmbientSound() {
        super.playAmbientSound();

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
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.AXOLOTL_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.AXOLOTL_DEATH;
    }

}