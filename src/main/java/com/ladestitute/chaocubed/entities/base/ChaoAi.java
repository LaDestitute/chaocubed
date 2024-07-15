package com.ladestitute.chaocubed.entities.base;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

import java.util.EnumSet;
import java.util.Optional;

public class ChaoAi {
    private static final UniformInt ADULT_FOLLOW_RANGE = UniformInt.of(10, 35);

    protected static Brain<?> makeBrain(Brain<ChaoEntity> pBrain) {
        initCoreActivity(pBrain);
        initIdleActivity(pBrain);
        initFightActivity(pBrain);
        pBrain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        pBrain.setDefaultActivity(Activity.IDLE);
        pBrain.useDefaultActivity();
        return pBrain;
    }

    static void initFightActivity(Brain<ChaoEntity> pBrain) {
        pBrain.addActivityAndRemoveMemoryWhenStopped(
                Activity.FIGHT,
                0,
                ImmutableList.of(
                        SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(ChaoAi::getSpeedModifierChasing),
                        MeleeAttack.create(20),
                        EraseMemoryIf.<Mob>create(BehaviorUtils::isBreeding, MemoryModuleType.ATTACK_TARGET)
                ),
                MemoryModuleType.ATTACK_TARGET
        );
    }

    static void initCoreActivity(Brain<ChaoEntity> pBrain) {
        pBrain.addActivity(
                Activity.CORE,
                0,
                ImmutableList.of(
                        new LookAtTargetSink(45, 90),
                        new MoveToTargetSink(),
                        new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS)
                )
        );
    }

    static void initIdleActivity(Brain<ChaoEntity> pBrain) {
        pBrain.addActivity(
                Activity.IDLE,
                ImmutableList.of(
                        Pair.of(0, SetEntityLookTargetSometimes.create(EntityType.PLAYER, 6.0F, UniformInt.of(30, 60))),
                        Pair.of(
                                2,
                                new RunOne<>(
                                        ImmutableList.of(
                                                Pair.of(new FollowTemptation(ChaoAi::getSpeedModifier), 1),
                                                Pair.of(BabyFollowAdult.create(ADULT_FOLLOW_RANGE, ChaoAi::getSpeedModifierFollowingAdult), 1)
                                        )
                                )
                        ),
                        Pair.of(3, StartAttacking.create(ChaoAi::findNearestValidAttackTarget)),
                        Pair.of(3, TryFindWater.create(5, 1F)),
                        Pair.of(
                                4,
                                new GateBehavior<>(
                                        ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT),
                                        ImmutableSet.of(),
                                        GateBehavior.OrderPolicy.ORDERED,
                                        GateBehavior.RunningPolicy.TRY_ALL,
                                        ImmutableList.of(
                                                Pair.of(RandomStroll.swim(0.63F), 1), // reduced time in water
                                                Pair.of(RandomStroll.stroll(0.63F, true), 2), // increased time on land
                                                Pair.of(SetWalkTargetFromLookTarget.create(ChaoAi::canSetWalkTargetFromLookTarget, ChaoAi::getSpeedModifier, 3), 3),
                                                Pair.of(BehaviorBuilder.triggerIf(Entity::isInWaterOrBubble), 3), // reduced water behavior
                                                Pair.of(BehaviorBuilder.triggerIf(Entity::onGround), 5)
                                        )
                                )
                        )
                )
        );
    }

    private static boolean canSetWalkTargetFromLookTarget(LivingEntity p_182381_) {
        Level level = p_182381_.level();
        Optional<PositionTracker> optional = p_182381_.getBrain().getMemory(MemoryModuleType.LOOK_TARGET);
        if (optional.isPresent()) {
            BlockPos blockpos = optional.get().currentBlockPosition();
            return level.isWaterAt(blockpos) == p_182381_.isInWaterOrBubble();
        } else {
            return false;
        }
    }

    public static void updateActivity(ChaoEntity pTestAmphiChaoEntity) {
        Brain<ChaoEntity> brain = pTestAmphiChaoEntity.getBrain();
        brain.setActiveActivityToFirstValid(ImmutableList.of(Activity.FIGHT, Activity.IDLE));
    }

    private static float getSpeedModifierChasing(LivingEntity p_149289_) {
        return 0.63F;
    }

    private static float getSpeedModifierFollowingAdult(LivingEntity p_149295_) {
        return 0.63F;
    }

    private static float getSpeedModifier(LivingEntity p_149301_) {
        return 0.63F;
    }

    private static Optional<? extends LivingEntity> findNearestValidAttackTarget(ChaoEntity p_149299_) {
        if (p_149299_.isTame() && p_149299_.getOwner() != null && p_149299_.getOwner().getLastHurtMob() != null) {
            return Optional.of(p_149299_.getOwner().getLastHurtMob());
        }
        return Optional.empty();
    }

    public static class CustomFollowOwnerGoal extends Goal {
        private final ChaoEntity chao;
        private LivingEntity owner;
        private final double followSpeed;
        private final float maxDist;
        private final float minDist;
        private final boolean teleportToLeaves;
        private int timeToRecalcPath;
        private float oldWaterCost;

        public CustomFollowOwnerGoal(ChaoEntity chao, double speed, float minDist, float maxDist, boolean teleport) {
            this.chao = chao;
            this.followSpeed = speed;
            this.minDist = minDist;
            this.maxDist = maxDist;
            this.teleportToLeaves = teleport;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity owner = this.chao.getOwner();
            if (owner == null) {
                return false;
            } else if (owner.isSpectator()) {
                return false;
            } else if (this.chao.isOrderedToSit()) {
                return false;
            } else if (this.chao.distanceToSqr(owner) < (this.minDist * this.minDist)) {
                return false;
            } else {
                this.owner = owner;
                return true;
            }
        }

        @Override
        public boolean canContinueToUse() {
            return !this.chao.getNavigation().isDone() && this.chao.distanceToSqr(this.owner) > (this.maxDist * this.maxDist) && !this.chao.isOrderedToSit();
        }

        @Override
        public void start() {
            this.timeToRecalcPath = 0;
            this.oldWaterCost = this.chao.getPathfindingMalus(BlockPathTypes.WATER);
            this.chao.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        }

        @Override
        public void stop() {
            this.owner = null;
            this.chao.getNavigation().stop();
            this.chao.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
        }

        @Override
        public void tick() {
            if (this.chao.isOrderedToSit()) {
                return;
            }

            this.chao.getLookControl().setLookAt(this.owner, 10.0F, (float) this.chao.getMaxHeadXRot());
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = this.adjustedTickDelay(10);
                if (!this.chao.isLeashed() && !this.chao.isPassenger()) {
                    if (this.chao.distanceToSqr(this.owner) >= 144.0D) {
                        this.tryToTeleportNearEntity();
                    } else {
                        this.chao.getNavigation().moveTo(this.owner, this.followSpeed);
                    }
                }
            }
        }

        private void tryToTeleportNearEntity() {
            BlockPos blockpos = this.owner.blockPosition();

            for (int i = 0; i < 10; ++i) {
                int j = this.randomIntInclusive(-3, 3);
                int k = this.randomIntInclusive(-1, 1);
                int l = this.randomIntInclusive(-3, 3);
                BlockPos blockpos1 = blockpos.offset(j, k, l);
                if (this.canTeleportTo(blockpos1)) {
                    this.chao.moveTo((double) ((float) blockpos1.getX() + 0.5F), (double) blockpos1.getY(), (double) ((float) blockpos1.getZ() + 0.5F), this.chao.getYRot(), this.chao.getXRot());
                    this.chao.getNavigation().stop();
                    return;
                }
            }

        }

        private boolean canTeleportTo(BlockPos pos) {
            BlockPathTypes pathtype = WalkNodeEvaluator.getBlockPathTypeStatic(this.chao.level(), pos.mutable());
            if (pathtype != BlockPathTypes.WALKABLE) {
                return false;
            } else {
                BlockState blockstate = this.chao.level().getBlockState(pos.below());
                if (!this.teleportToLeaves && blockstate.getBlock() instanceof LeavesBlock) {
                    return false;
                } else {
                    BlockPos blockpos = pos.subtract(this.chao.blockPosition());
                    return this.chao.level().noCollision(this.chao, this.chao.getBoundingBox().move(blockpos));
                }
            }
        }

        private int randomIntInclusive(int min, int max) {
            return this.chao.getRandom().nextInt(max - min + 1) + min;
        }
    }

    public static class CustomSitWhenOrderedToGoal extends Goal {
        private final ChaoEntity chao;

        public CustomSitWhenOrderedToGoal(ChaoEntity chao) {
            this.chao = chao;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            if (!this.chao.isTame()) {
                return false;
            } else if (this.chao.isInWaterOrBubble()) {
                return false;
            } else if (!this.chao.onGround()) {
                return false;
            } else {
                return this.chao.isOrderedToSit();
            }
        }

        @Override
        public void start() {
            this.chao.getNavigation().stop();
            this.chao.setOrderedToSit(true);
        }

        @Override
        public void stop() {
            this.chao.setOrderedToSit(false);
        }
    }
}