package com.ladestitute.chaocubed.entities.chao;

import com.ladestitute.chaocubed.entities.base.TestAmphiChaoEntity;
import com.ladestitute.chaocubed.util.ChaoVariant;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForgeMod;

import java.util.Random;

public class NeutralChaoEntity extends TestAmphiChaoEntity {

    public NeutralChaoEntity(EntityType<? extends TestAmphiChaoEntity> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new MoveControl(this);
        this.rollForShiny();
//        if(this.power-points > 9000)
//        {
//            this.setVariant(ChaoVariant.NORMAL_TWOTONE_NEUTRAL_POWER);
//        }
         if(this.shiny)
        {
            this.setVariant(ChaoVariant.SHINY_NORMAL_TWOTONE);
            this.swim_points=100;
            this.entityData.set(RUN_POINTS, 50);
        }
        else this.setVariant(ChaoVariant.NORMAL_TWOTONE);
        this.getAttribute(NeoForgeMod.SWIM_SPEED.value()).setBaseValue(1.0D);
    }

    private void rollForShiny() {
        Random random = new Random();
        if(random.nextDouble() < 0.2) {
        this.shiny=true;
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMobAttributes()
                .add(Attributes.MAX_HEALTH, TestAmphiChaoEntity.DEFAULT_HEALTH)
                .add(Attributes.MOVEMENT_SPEED, 0.5D)
                .add(NeoForgeMod.SWIM_SPEED.value(), 1.0D)
                .add(Attributes.FOLLOW_RANGE, 48.0);
    }

    @Override
    public void tick() {
        super.tick();

        // Dynamic speed adjustments based on stats
        if (this.run_points >= 1334) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25D * 2);
        } else if (run_points >= 667) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25D * 1.5);
        } else {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        }

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

        if (this.swim_points >= 667) {
            //Todo: reimplement
          //  this.getAttribute(Attributes.).setBaseValue(this.getAttribute(Attributes.WATER_MOVEMENT_EFFICIENCY).getValue() * 1.5);
        }
    }

}