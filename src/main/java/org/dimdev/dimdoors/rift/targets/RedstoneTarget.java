package org.dimdev.dimdoors.rift.targets;

import net.minecraft.util.math.Direction;

public interface RedstoneTarget extends Target {
    boolean addRedstonePower(Direction relativeFacing, int strength);

    void subtractRedstonePower(Direction relativeFacing, int strength);
}
