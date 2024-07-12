package com.ladestitute.chaocubed.entities.base;

import com.ladestitute.chaocubed.ChaoCubedConfig;
import com.ladestitute.chaocubed.ChaoCubedMain;
import com.ladestitute.chaocubed.util.ChaoVariant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public abstract class ChaoEntity extends TamableAnimal {
    // Boilerplate code for all Chao entities to inherit
    private ChaoVariant variant;
    public int run_points;
    public int swim_points;
    public boolean shiny;
    //Todo: fix shiny not saving on world exit, will use neoforge's attachments system
//    private static final EntityDataAccessor<Integer> SWIM_LEVEL = SynchedEntityData.defineId(ChaoEntity.class, EntityDataSerializers.INT);
//    private static final EntityDataAccessor<Integer> SWIM_POINTS = SynchedEntityData.defineId(ChaoEntity.class, EntityDataSerializers.INT);
//    private static final EntityDataAccessor<Integer> FLY_LEVEL = SynchedEntityData.defineId(ChaoEntity.class, EntityDataSerializers.INT);
//    private static final EntityDataAccessor<Integer> FLY_POINTS = SynchedEntityData.defineId(ChaoEntity.class, EntityDataSerializers.INT);
//    private static final EntityDataAccessor<Integer> RUN_LEVEL = SynchedEntityData.defineId(ChaoEntity.class, EntityDataSerializers.INT);
//    private static final EntityDataAccessor<Integer> RUN_POINTS = SynchedEntityData.defineId(ChaoEntity.class, EntityDataSerializers.INT);
//    private static final EntityDataAccessor<Integer> POWER_LEVEL = SynchedEntityData.defineId(ChaoEntity.class, EntityDataSerializers.INT);
//    private static final EntityDataAccessor<Integer> POWER_POINTS = SynchedEntityData.defineId(ChaoEntity.class, EntityDataSerializers.INT);
//    private static final EntityDataAccessor<Integer> STAMINA_LEVEL = SynchedEntityData.defineId(ChaoEntity.class, EntityDataSerializers.INT);
//    private static final EntityDataAccessor<Integer> STAMINA_POINTS = SynchedEntityData.defineId(ChaoEntity.class, EntityDataSerializers.INT);
//    private static final EntityDataAccessor<Boolean> SHINY = SynchedEntityData.defineId(ChaoEntity.class, EntityDataSerializers.BOOLEAN);

    // Note: the damage threshold for a chao's happiness reduction
    // should be its max health divided by 2.12 (rounded down)
    // Still haven't decided how much Stamina will contribute to health

    // Default health and happiness reduction damage threshold for all Chao
    // save for adults/second-stage evos, chao with high Stamina, etc
    public static final float DEFAULT_HEALTH = 18F;
    private final float HAPPINESS_REDUCTION_DAMAGE_THRESHOLD = Math.round((float) (DEFAULT_HEALTH/2.12));

    protected ChaoEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setTame(false);
        this.moveControl = new MoveControl(this);
        this.variant = ChaoVariant.NORMAL;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ChaoEntity.ChaoPanicGoal(1.5));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.swim_points = compound.getInt("swim_points");
        this.run_points = compound.getInt("run_points");
        this.shiny = compound.getBoolean("shiny");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("swim_points", this.swim_points);
        compound.putInt("run_points", this.run_points);
        compound.putBoolean("shiny", this.shiny);
    }

    @Override
    public void tick() {
        super.tick();
    }

    public  TagKey<Item> CHAO_FRUIT = ItemTags.create(new ResourceLocation(ChaoCubedMain.MODID, "chao_fruit"));

    @Override
    public boolean isFood(ItemStack pStack) {
        return pStack.is(CHAO_FRUIT);
    }

    @Nullable
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
        if (this.random.nextInt(3) == 0  && !net.neoforged.neoforge.event.EventHooks.onAnimalTame(this, pPlayer)) {
            this.tame(pPlayer);
            this.navigation.stop();
            this.setTarget(null);
            this.setOrderedToSit(true);
            this.level().broadcastEntityEvent(this, (byte)7);
        } else {
            this.level().broadcastEntityEvent(this, (byte)6);
        }
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return ChaoCubedConfig.maxChaoGroupSize;
    }

    class ChaoPanicGoal extends PanicGoal {
        public ChaoPanicGoal(double pSpeedModifier) {
            super(ChaoEntity.this, pSpeedModifier);
        }

        @Override
        protected boolean shouldPanic() {
            return this.mob.isFreezing() || this.mob.isOnFire();
        }
    }
}
