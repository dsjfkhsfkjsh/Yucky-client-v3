package com.yucky.client.freecam;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

/**
 * A camera-only entity. It is never added to the world and therefore never
 * participates in entity tracking or server networking.
 */
public final class FreecamEntity extends Entity {
    public FreecamEntity(World world) {
        super(EntityType.ARMOR_STAND, world);
        setNoGravity(true);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        // No synchronized data is needed for a camera-only entity.
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        // Never saved.
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        // Never saved.
    }
}
