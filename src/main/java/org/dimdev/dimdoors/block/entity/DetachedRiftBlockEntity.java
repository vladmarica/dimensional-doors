package org.dimdev.dimdoors.block.entity;

import lombok.Setter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.dimdoors.ModConfig;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.util.Location;
import org.dimdev.util.TeleportUtil;
import org.dimdev.annotatednbt.AnnotatedNbt;

import java.util.Random;


public class DetachedRiftBlockEntity extends RiftBlockEntity implements Tickable {
    private static final Random random = new Random();

    @Saved public boolean closing = false;
    @Saved public boolean stabilized = false;
    @Saved public int spawnedEndermanId = 0;
    @Saved public float size = 0;

    @Setter private boolean unregisterDisabled = false;

    @Environment(EnvType.CLIENT)
    public double renderAngle;

    public DetachedRiftBlockEntity() {
        super(ModBlockEntityTypes.ENTRANCE_RIFT);
    }

    @Override
    public void tick() {
        if (world.getBlockState(pos).getBlock() != ModBlocks.DETACHED_RIFT) {
            markInvalid();
            return;
        }

        if (!world.isClient && random.nextDouble() < ModConfig.GENERAL.endermanSpawnChance) {
            EndermanEntity enderman = EntityType.ENDERMAN.spawn(world, null, null, null, pos, SpawnType.STRUCTURE, false, false);

            if (random.nextDouble() < ModConfig.GENERAL.endermanAggressiveChance) {
                enderman.setTarget(world.getClosestPlayer(enderman, 50));
            }
        }

        if (closing) {
            if (size > 0) {
                size -= ModConfig.GENERAL.riftCloseSpeed;
            } else {
                world.removeBlock(pos, false);
            }
        } else if (!stabilized) {
            size += ModConfig.GENERAL.riftGrowthSpeed / (size + 1);
        }
    }

    public void setClosing(boolean closing) {
        this.closing = closing;
        markDirty();
    }

    public void setStabilized(boolean stabilized) {
        this.stabilized = stabilized;
        markDirty();
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        AnnotatedNbt.load(this, tag);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag = super.toTag(tag);
        AnnotatedNbt.save(this, tag);
        return tag;
    }

    @Override
    public boolean isDetached() {
        return true;
    }

    @Override
    public void unregister() {
        if (!unregisterDisabled) {
            super.unregister();
        }
    }

    @Override
    public boolean receiveEntity(Entity entity, float relativeYaw, float relativePitch) {
        TeleportUtil.teleport(entity, new Location(world, pos), entity.yaw, entity.pitch);
        return true;
    }
}
