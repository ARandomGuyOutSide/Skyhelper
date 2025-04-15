package org.polyfrost.skyhelper.util;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class InventoryStuff {
    private static Minecraft mc = Minecraft.getMinecraft();

    public static void clickOpenContainerSlot(final int slot) {
        mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, slot, 0, 0, mc.thePlayer);
    }
}
