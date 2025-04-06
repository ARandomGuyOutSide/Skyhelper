package org.polyfrost.skyhelper.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

public class Player {
    private static Minecraft mc = Minecraft.getMinecraft();

    public static void warpToForge()
    {
        //mc.thePlayer.sendChatMessage("/warp forge");
        mc.thePlayer.sendChatMessage("/tp @p 0 149 -69");
    }

    public static BlockPos getPlayerPos()
    {
        double x, y, z;
        x = mc.thePlayer.posX;
        y = mc.thePlayer.posY;
        z = mc.thePlayer.posZ;

        return new BlockPos(x, y, z);
    }

    public static void openSkyblockMenu()
    {
        // Selects the skyblock Menu and opens the hotm tree
        mc.displayGuiScreen(new GuiInventory(mc.thePlayer));
    }

    public static void getMiningTool()
    {
        for (int i = 0; i < 8; i++) { // Wir nutzen hier eine normale for-Schleife für den Index
            ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
            if (itemStack != null) {
                Item item = itemStack.getItem();
                if (itemStack.getDisplayName().substring(2).equals("Divan's Drill")) {
                    mc.thePlayer.addChatComponentMessage(new ChatComponentText("Divan drill found at: " + i));
                    mc.thePlayer.inventory.currentItem = i;
                    break;
                }
                else if (itemStack.getDisplayName().substring(2).equals("Titanium Drill DR-X655")) {
                    mc.thePlayer.addChatComponentMessage(new ChatComponentText("Titanium drill found at: " + i));
                    mc.thePlayer.inventory.currentItem = i;
                    break;
                }
                else if (itemStack.getDisplayName().substring(2).equals("Gemstone Gauntlet")) {
                    mc.thePlayer.addChatComponentMessage(new ChatComponentText("Gemstone Gauntlet found at: " + i));
                    mc.thePlayer.inventory.currentItem = i;
                    break;
                }
                else if (item instanceof ItemPickaxe) {
                    mc.thePlayer.addChatComponentMessage(new ChatComponentText("Pickaxe gefunden in Slot: " + i));
                    mc.thePlayer.inventory.currentItem = i;
                    break;
                }

            }
        }
    }
}
