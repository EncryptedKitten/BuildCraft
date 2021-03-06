/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL. Please check the contents
 * of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt */
package buildcraft.core.lib.fluids;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;

import buildcraft.core.lib.BlockTileCache;
import buildcraft.core.lib.inventory.InvUtils;

public final class TankUtils {

    /** Deactivate constructor */
    private TankUtils() {}

    public static boolean handleRightClick(IFluidHandler tank, EnumFacing side, EntityPlayer player, boolean fill, boolean drain) {
        if (player == null || tank == null) {
            return false;
        }
        ItemStack current = player.inventory.getCurrentItem();
        if (current != null) {

            FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(current);

            if (fill && liquid != null) {
                int used = tank.fill(side, liquid, true);

                if (used > 0) {
                    if (!player.capabilities.isCreativeMode) {
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, InvUtils.consumeItem(current));
                        player.inventory.markDirty();
                    }
                    return true;
                }

            } else if (drain) {

                FluidStack available = tank.drain(side, Integer.MAX_VALUE, false);
                if (available != null) {
                    ItemStack filled = FluidContainerRegistry.fillFluidContainer(available, current);

                    liquid = FluidContainerRegistry.getFluidForFilledItem(filled);
                    if (liquid != null) {

                        if (current.stackSize > 1) {
                            if (!player.inventory.addItemStackToInventory(filled)) {
                                return false;
                            }
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, InvUtils.consumeItem(current));
                            player.inventory.markDirty();
                        } else {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, InvUtils.consumeItem(current));
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, filled);
                            player.inventory.markDirty();
                        }

                        tank.drain(side, liquid.amount, true);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static Block getFluidBlock(Fluid fluid, boolean moving) {
        if (fluid == FluidRegistry.WATER) {
            return moving ? Blocks.FLOWING_WATER : Blocks.WATER;
        }
        if (fluid == FluidRegistry.LAVA) {
            return moving ? Blocks.FLOWING_LAVA : Blocks.LAVA;
        }
        return fluid.getBlock();
    }

    public static void pushFluidToConsumers(IFluidTank tank, int flowCap, BlockTileCache[] tileBuffer) {
        int amountToPush = flowCap;
        for (EnumFacing side : EnumFacing.VALUES) {
            FluidStack fluidStack = tank.drain(amountToPush, false);
            if (fluidStack != null && fluidStack.amount > 0) {
                TileEntity tile = tileBuffer[side.ordinal()].getTile();
                if (tile instanceof IFluidHandler) {
                    int used = ((IFluidHandler) tile).fill(side.getOpposite(), fluidStack, true);
                    if (used > 0) {
                        amountToPush -= used;
                        tank.drain(used, true);
                        if (amountToPush <= 0) {
                            return;
                        }
                    }
                }
            }
        }
    }
}
