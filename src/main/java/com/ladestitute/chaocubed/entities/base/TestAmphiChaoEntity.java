package com.ladestitute.chaocubed.entities.base;

import com.ladestitute.chaocubed.ChaoCubedConfig;
import com.ladestitute.chaocubed.ChaoCubedMain;
import com.ladestitute.chaocubed.util.ChaoVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public abstract class TestAmphiChaoEntity extends TamableAnimal {
    private ChaoVariant variant;
    public int run_points;
    public int swim_points;
    public boolean shiny;
    public static final EntityDataAccessor<Integer> RUN_POINTS = SynchedEntityData.defineId(TestAmphiChaoEntity.class, EntityDataSerializers.INT);

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
        this.entityData.define(RUN_POINTS, 0);
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
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1) {
            @Override
            public boolean canUse() {
                return super.canUse() && TestAmphiChaoEntity.this.random.nextFloat() <= 0.50;
            }
        });
        this.goalSelector.addGoal(3, new RandomSwimmingGoal(this,0,1));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.2, false));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
    }

    @Override
    public void baseTick() {
        super.baseTick();
        int airSupply = this.getAirSupply();
        this.handleAirSupply(airSupply);
    }

    public void travel(Vec3 pTravelVector) {
        if (this.isAlive()) {
                super.travel(pTravelVector);
        }
    }

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
    public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
        return false;
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return ChaoCubedConfig.maxChaoGroupSize;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (this.isTame()) {
            if (itemStack.isEmpty() && player.isCrouching()) {
                this.setOrderedToSit(!this.isOrderedToSit());
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            } else if (itemStack.is(CHAO_FRUIT) && this.getHealth() < this.getMaxHealth()) {
                if (!player.getAbilities().instabuild) {
                    itemStack.shrink(1);
                }
                this.heal(5.0F);
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        } else if (itemStack.is(CHAO_FRUIT)) {
            if (!player.getAbilities().instabuild) {
                itemStack.shrink(1);
            }
            if (!this.level().isClientSide) {
                if (this.random.nextInt(2) == 0) {
                    this.tame(player);
                    this.navigation.stop();
                    this.setTarget(null);
                    this.level().broadcastEntityEvent(this, (byte) 7);
                } else {
                    this.level().broadcastEntityEvent(this, (byte) 6);
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

}