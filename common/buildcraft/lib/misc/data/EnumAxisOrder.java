/*
 * Copyright (c) 2017 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */

package buildcraft.lib.misc.data;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import net.minecraft.util.EnumFacing.Axis;

import buildcraft.lib.misc.data.AxisOrder.Inversion;

public enum EnumAxisOrder {
    XYZ(Axis.X, Axis.Y, Axis.Z),
    XZY(Axis.X, Axis.Z, Axis.Y),
    YXZ(Axis.Y, Axis.X, Axis.Z),
    YZX(Axis.Y, Axis.Z, Axis.X),
    ZXY(Axis.Z, Axis.X, Axis.Y),
    ZYX(Axis.Z, Axis.Y, Axis.X);

    public static final EnumAxisOrder[] VALUES = values();

    private static final Map<String, EnumAxisOrder> orderMap;

    static {
        ImmutableMap.Builder<String, EnumAxisOrder> builder = ImmutableMap.builder();
        for (EnumAxisOrder order : values()) {
            builder.put(order.name(), order);
        }
        orderMap = builder.build();
    }

    public final Axis first, second, third;

    EnumAxisOrder(Axis a, Axis b, Axis c) {
        this.first = a;
        this.second = b;
        this.third = c;
    }

    public static EnumAxisOrder getOrder(String name) {
        EnumAxisOrder order = orderMap.get(name);
        if (order == null) {
            order = XZY;
        }
        return order;
    }

    public static EnumAxisOrder getOrder(Axis first, Axis b) {
        switch (first) {
            case X:
                return b == Axis.Y ? XYZ : XZY;
            case Y:
                return b == Axis.X ? YXZ : YZX;
            default:
                return b == Axis.X ? ZXY : ZYX;
        }
    }

    public AxisOrder getMinToMaxOrder() {
        return AxisOrder.getFor(this, Inversion.PPP);
    }

    public AxisOrder getMaxToMinOrder() {
        return AxisOrder.getFor(this, Inversion.NNN);
    }
}
