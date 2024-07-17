package com.ladestitute.chaocubed.entities.chao;

import com.ladestitute.chaocubed.entities.base.ChaoEntity;
import com.ladestitute.chaocubed.util.ChaoVariant;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class NeutralChaoEntity extends ChaoEntity {

    public NeutralChaoEntity(EntityType<? extends ChaoEntity> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new MoveControl(this);
        this.rollForShiny();
        if(!this.level().isClientSide()) {
            if (this.getEntityData().get(RUN_POINTS) > 9000) {
                this.setVariant(ChaoVariant.NORMAL_TWOTONE_NEUTRAL_POWER);
            }
            if (this.getEntityData().get(SHINY)) {
                this.setVariant(ChaoVariant.SHINY_NORMAL_TWOTONE);
                this.entityData.set(POWER_POINTS, 200);
                this.entityData.set(FLY_POINTS, 200);
                this.entityData.set(SWIM_POINTS, 100);
                this.entityData.set(RUN_POINTS, 50);
            } else this.setVariant(ChaoVariant.NORMAL_TWOTONE);
        }
    }

    private void rollForShiny() {
        Random random = new Random();
        if(random.nextDouble() < 0.2) {
            if(!this.level().isClientSide()) {
                this.getEntityData().set(SHINY, true);
            }
        }
    }

    static double startingspeed = 0.30D;

    public static AttributeSupplier.Builder createAttributes() {
        return createMobAttributes()
                .add(Attributes.MAX_HEALTH, 18F)
                .add(Attributes.MOVEMENT_SPEED, startingspeed)
                .add(NeoForgeMod.SWIM_SPEED.value(), startingspeed)
                .add(Attributes.FOLLOW_RANGE, 20.0);
    }

    @Override
    public void tick() {
        super.tick();

        // Dynamic speed adjustments based on run stat
        if (this.getRunPoints() >= 2668) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(startingspeed * 3);
        }
        else if (this.getRunPoints() >= 2001) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(startingspeed * 2.5);
        }
        else if (this.getRunPoints() >= 1334) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(startingspeed * 2);
        } else if (this.getRunPoints() >= 667) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(startingspeed * 1.5);
        } else {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(startingspeed);
        }

        // Dynamic attack damage adjustments based on power stat
        if (this.getPowerPoints() >= 2400) {
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(4D);
        } else if (this.getPowerPoints() >= 2000) {
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(3.5D);
        } else if (this.getPowerPoints() >= 1600) {
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(3D);
        } else if (this.getPowerPoints() >= 1200) {
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(2.5D);
        } else if (this.getPowerPoints() >= 800) {
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(2D);
        } else if (this.getPowerPoints() >= 400) {
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(1.5D);
        } else {
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(1.0D);
        }

        // Dynamic swimming speed adjustments based on swim stat
        if (this.getRunPoints() >= 2668) {
            this.getAttribute(NeoForgeMod.SWIM_SPEED.value()).setBaseValue(startingspeed * 3);
        }
        else if (this.getRunPoints() >= 2001) {
            this.getAttribute(NeoForgeMod.SWIM_SPEED.value()).setBaseValue(startingspeed * 2.5);
        }
        else if (this.getRunPoints() >= 1334) {
            this.getAttribute(NeoForgeMod.SWIM_SPEED.value()).setBaseValue(startingspeed * 2);
        } else if (this.getRunPoints() >= 667) {
            this.getAttribute(NeoForgeMod.SWIM_SPEED.value()).setBaseValue(startingspeed * 1.5);
        } else {
            this.getAttribute(NeoForgeMod.SWIM_SPEED.value()).setBaseValue(startingspeed);
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if(!player.level().isClientSide()) {
            this.entityData.set(POWER_POINTS, 200);
            this.entityData.set(FLY_POINTS, 200);
            this.entityData.set(SWIM_POINTS, 100);
            this.entityData.set(RUN_POINTS, 50);
            player.sendSystemMessage(Component.literal("FLY POINTS IS: " + this.getFlyPoints()));
        }
        return super.mobInteract(player, hand);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return null;
    }
}