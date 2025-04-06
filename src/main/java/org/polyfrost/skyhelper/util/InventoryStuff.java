package org.polyfrost.skyhelper.util;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class InventoryStuff {
    private static Minecraft mc = Minecraft.getMinecraft();

    public static List<ItemStack> scanInventory() {
        List<ItemStack> itemStacks = new ArrayList<>();
        // Scans the hotbar from 0 to 8
        for (int i = 0; i <= 8; i++) {
            ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
            if (itemStack != null)
                itemStacks.add(itemStack);
        }

        // Scans the rest of the inventory from 9 to 35
        for (int i = 9; i < mc.thePlayer.inventory.mainInventory.length; i++) {
            ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
            if (itemStack != null)
                itemStacks.add(itemStack);
        }

        return itemStacks;
    }

    public static void clickOpenContainerSlot(final int slot) {
        mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, slot, 0, 0, mc.thePlayer);
    }
}
