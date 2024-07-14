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
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class ChaoAi {
    private static final UniformInt ADULT_FOLLOW_RANGE = UniformInt.of(10, 35);
    private static final ChaoFollowOwnerBehavior FOLLOW_OWNER_BEHAVIOR = new ChaoFollowOwnerBehavior();
    public static final ChaoSitWhenOrderedBehavior SIT_WHEN_ORDERED_BEHAVIOR = new ChaoSitWhenOrderedBehavior();

    protected static Brain<?> makeBrain(Brain<TestAmphiChaoEntity> pBrain) {
        initCoreActivity(pBrain);
        initIdleActivity(pBrain);
        initFightActivity(pBrain);
        pBrain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        pBrain.setDefaultActivity(Activity.IDLE);
        pBrain.useDefaultActivity();
        return pBrain;
    }

    static void initFightActivity(Brain<TestAmphiChaoEntity> pBrain) {
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

    static void initCoreActivity(Brain<TestAmphiChaoEntity> pBrain) {
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

    static void initIdleActivity(Brain<TestAmphiChaoEntity> pBrain) {
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
                        Pair.of(3, TryFindWater.create(3, 1F)),
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
                                                Pair.of(BehaviorBuilder.triggerIf(Entity::onGround), 5),
                                                Pair.of(FOLLOW_OWNER_BEHAVIOR, 1), // Add follow owner behavior
                                                Pair.of(SIT_WHEN_ORDERED_BEHAVIOR, 1) // Add sit when ordered behavior
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

    public static void updateActivity(TestAmphiChaoEntity entity) {
        Brain<TestAmphiChaoEntity> brain = (Brain<TestAmphiChaoEntity>) entity.getBrain();
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

    private static Optional<? extends LivingEntity> findNearestValidAttackTarget(TestAmphiChaoEntity p_149299_) {
        if (p_149299_.isTame() && p_149299_.getOwner() != null && p_149299_.getOwner().getLastHurtMob() != null) {
            return Optional.of(p_149299_.getOwner().getLastHurtMob());
        }
        return Optional.empty();
    }
}