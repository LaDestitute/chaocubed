package com.ladestitute.chaocubed.entities.base;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class ChaoFollowOwnerGoal extends Goal {
    //A custom version of follow owner goal that allows for the amphibious navigation type
    private final TamableAnimal tamable;
    private LivingEntity owner;
    private final double followSpeed;
    private final float maxDist;
    private final float minDist;
    private Path path;
    private int timeToRecalcPath;
    private final boolean teleportToLeaves;

    public ChaoFollowOwnerGoal(TamableAnimal tamable, double followSpeed, float minDist, float maxDist, boolean teleportToLeaves) {
        this.tamable = tamable;
        this.followSpeed = followSpeed;
        this.minDist = minDist;
        this.maxDist = maxDist;
        this.teleportToLeaves = teleportToLeaves;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity owner = this.tamable.getOwner();
        if (owner == null) {
            return false;
        } else if (this.tamable.isOrderedToSit()) {
            return false;
        } else if (this.tamable.distanceToSqr(owner) < (double)(this.minDist * this.minDist)) {
            return false;
        } else {
            this.owner = owner;
            return true;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return !this.tamable.getNavigation().isDone() && this.tamable.distanceToSqr(this.owner) > (double)(this.maxDist * this.maxDist) && !this.tamable.isOrderedToSit();
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
    }

    @Override
    public void stop() {
        this.owner = null;
        this.tamable.getNavigation().stop();
    }

    @Override
    public void tick() {
        this.tamable.getLookControl().setLookAt(this.owner, 10.0F, (float)this.tamable.getMaxHeadXRot());
        if (!this.tamable.isOrderedToSit()) {
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = 10;
                if (!this.tamable.isLeashed() && !this.tamable.isPassenger()) {
                    if (this.tamable.distanceToSqr(this.owner) >= 144.0D) {
                        this.tryToTeleportNearEntity();
                    } else {
                        this.path = this.tamable.getNavigation().createPath(this.owner, 0);
                        this.tamable.getNavigation().moveTo(this.path, this.followSpeed);
                    }
                }
            }
        }
    }

    private void tryToTeleportNearEntity() {
        Vec3 vec3 = new Vec3(this.owner.getX() - this.tamable.getX(), this.owner.getY() - this.tamable.getY(), this.owner.getZ() - this.tamable.getZ());
        vec3 = vec3.normalize().scale(2.0D);
        this.tamable.teleportTo(this.owner.getX() - vec3.x, this.owner.getY() - vec3.y, this.owner.getZ() - vec3.z);
        this.tamable.getNavigation().stop();
    }
}
