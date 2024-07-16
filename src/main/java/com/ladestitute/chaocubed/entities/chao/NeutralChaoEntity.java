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
        if(this.getEntityData().get(RUN_POINTS) > 9000)
        {
            this.setVariant(ChaoVariant.NORMAL_TWOTONE_NEUTRAL_POWER);
        }
         if(this.getEntityData().get(SHINY))
        {
         //   this.setVariant(ChaoVariant.SHINY_NORMAL_TWOTONE);
            if(!this.level().isClientSide()) {
                this.entityData.set(POWER_POINTS, 200);
                this.entityData.set(FLY_POINTS, 200);
                this.entityData.set(SWIM_POINTS, 100);
                this.entityData.set(RUN_POINTS, 50);
            }
        }
     //   else this.setVariant(ChaoVariant.NORMAL_TWOTONE);
    }

    private void rollForShiny() {
        Random random = new Random();
        if(random.nextDouble() < 0.2) {
            if(!this.level().isClientSide()) {
                this.getEntityData().set(SHINY, true);
            }
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMobAttributes()
                .add(Attributes.MAX_HEALTH, 18F)
                .add(Attributes.MOVEMENT_SPEED, 0.30D)
                .add(NeoForgeMod.SWIM_SPEED.value(), 0.30D)
                .add(Attributes.FOLLOW_RANGE, 20.0);
    }

    @Override
    public void tick() {
        super.tick();

        // Dynamic speed adjustments based on stats
//        if (this.run_points >= 1334) {
//            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25D * 2);
//        } else if (run_points >= 667) {
//            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25D * 1.5);
//        } else {
//            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25D);
//        }

        // Dynamic attack damage adjustments based on power stat
        //todo: reimplement
//        if (this.getPowerPoints() >= 400) {
//            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(1.0D * 1.5);
//        } else {
//            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(1.0D);
//        }

        // Switch animation states based on stats
//        if (this.getFlyPoints() >= 667) {
//            this.getAttribute(Attributes.FLYING_SPEED).setBaseValue(this.getAttribute(Attributes.FLYING_SPEED).getValue() * 1.5);
//        }

//        if (this.swim_points >= 667) {
//            //Todo: reimplement
//          //  this.getAttribute(Attributes.).setBaseValue(this.getAttribute(Attributes.WATER_MOVEMENT_EFFICIENCY).getValue() * 1.5);
//        }
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