package org.polyfrost.skyhelper.player;

import cc.polyfrost.oneconfig.libs.dataflow.qual.SideEffectFree;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.polyfrost.skyhelper.MainController;
import org.polyfrost.skyhelper.util.Chatter;

import java.util.Random;

public class Player {
    private static Minecraft mc = Minecraft.getMinecraft();

    private static float prevYaw;
    private static float prevPitch;
    private static float targetYaw;
    private static float targetPitch;
    private static boolean isRotating = false;
    private static long startTime;
    private static long rotationDuration = 300;
    private static boolean isMovingForward = false;
    private static int movementTicks = 0;
    private static final int MAX_MOVEMENT_TICKS = 10; // 0.5 seconds at 20 ticks per second
    private static double lastX, lastZ;

    private static boolean pickaxeSkillReady = true;

    public static void warpToForge() {
        //mc.thePlayer.sendChatMessage("/warp forge");
        mc.thePlayer.sendChatMessage("/tp @p 0 149 -69");
    }

    public static void openSkyblockMenu() {
        // Opens the inventory screen (to later select SkyBlock Menu manually)
        mc.displayGuiScreen(new GuiInventory(mc.thePlayer));
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        // Handle rotation updates
        updateRotation();

        // Handle forward movement after mining
        if (isMovingForward) {
            Minecraft mc = Minecraft.getMinecraft();
            movementTicks++;

            // Check if we've moved enough or time is up
            double distanceMoved = Math.sqrt(
                    Math.pow(mc.thePlayer.posX - lastX, 2) +
                            Math.pow(mc.thePlayer.posZ - lastZ, 2)
            );

            if (movementTicks >= MAX_MOVEMENT_TICKS || distanceMoved > 0.5) {
                // Stop movement
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
                isMovingForward = false;

                if (MainController.config.isDebugMode()) {
                    String reason = movementTicks >= MAX_MOVEMENT_TICKS ? "time limit" : "moved enough";
                    Chatter.sendDebutChat("§7Stopped movement after " + movementTicks + " ticks (" + reason + ")");
                }
            }
        }
    }

    public static void moveWhileMining() {
        if(MainController.config.isSneakWhileMining())
        {
            if (MainController.config.isMoveWhileMining()) {
                Minecraft mc = Minecraft.getMinecraft();
                Random random = new Random();

                // Array mit allen vier Richtungstasten
                KeyBinding[] movementKeys = {
                        mc.gameSettings.keyBindForward,    // W
                        mc.gameSettings.keyBindLeft,       // A
                        mc.gameSettings.keyBindBack,       // S
                        mc.gameSettings.keyBindRight       // D
                };

                // Zufällige Taste auswählen (jede mit 25% Chance)
                int randomKeyIndex = random.nextInt(4);
                KeyBinding selectedKey = movementKeys[randomKeyIndex];

                // Die ausgewählte Taste drücken
                KeyBinding.setKeyBindState(selectedKey.getKeyCode(), true);

                // Einen Thread erstellen, um die Taste nach 0,1 Sekunden loszulassen
                new Thread(() -> {
                    try {
                        Thread.sleep(100);

                        // Taste loslassen
                        KeyBinding.setKeyBindState(selectedKey.getKeyCode(), false);

                        if (MainController.config.isDebugMode()) {
                            String direction = "";
                            if (selectedKey == mc.gameSettings.keyBindForward) direction = "vorwärts";
                            else if (selectedKey == mc.gameSettings.keyBindLeft) direction = "links";
                            else if (selectedKey == mc.gameSettings.keyBindBack) direction = "rückwärts";
                            else if (selectedKey == mc.gameSettings.keyBindRight) direction = "rechts";

                            Chatter.sendDebutChat("§7Bewegte sich " + direction + " für 0,1 Sekunden");
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }

    }

    public static void smoothRotateToBlock(double blockX, double blockY, double blockZ) {
        rotationDuration = (10 - MainController.config.getRotationSpeed()) * 75L;
        double playerX = mc.thePlayer.posX;
        double playerY = mc.thePlayer.posY + mc.thePlayer.eyeHeight;
        double playerZ = mc.thePlayer.posZ;

        // Adjust player eye height when sneaking
        if (MainController.config.isSneakWhileMining()) {
            // When sneaking, the eye height is reduced by ~0.3 blocks
            playerY -= 0.3;

            // Actually press the sneak key
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
        } else {
            // Make sure the player is not sneaking
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
        }

        double targetX = blockX - playerX;
        double targetY = blockY - playerY;
        double targetZ = blockZ - playerZ;

        double yaw = Math.atan2(targetZ, targetX) * 180.0 / Math.PI - 90.0;

        // Calculate pitch (vertical rotation)
        double horizontalDistance = Math.sqrt(targetX * targetX + targetZ * targetZ);
        double pitch = -Math.atan2(targetY, horizontalDistance) * 180.0 / Math.PI;

        // Store start positions
        prevYaw = mc.thePlayer.rotationYaw;
        prevPitch = mc.thePlayer.rotationPitch;

        // Normalize yaw for shortest rotation path
        targetYaw = normalizeAngle((float) yaw, prevYaw);
        targetPitch = (float) pitch;

        // Start rotation
        isRotating = true;
        startTime = System.currentTimeMillis();
    }

    @SubscribeEvent
    public void onMessageReceived(ClientChatReceivedEvent event) {
        String message = ChatFormatting.stripFormatting(event.message.getUnformattedText());

        try {
            if (message.contains(":") || message.contains(">")) return;
            if (message.startsWith("You used your")) {
                pickaxeSkillReady = false;
            } else if (message.endsWith("is now available!")) {
                pickaxeSkillReady = true;
            } else if (message.startsWith("Your pickaxe ability is on")) {
                pickaxeSkillReady = false;
            }
        } catch (Exception ignored) {

        }
    }

    public static boolean useMiningSpeedBoost() {
        if (pickaxeSkillReady)
            if (getMiningTool() > -1)
            {
                mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getStackInSlot(getMiningTool()));
                return false;
            }
        return true;
    }

    public static int getMiningTool() {
        for (int i = 0; i < 8; i++) { // We use a regular for-loop here for the index
            ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
            if (itemStack != null) {
                Item item = itemStack.getItem();
                String displayName = itemStack.getDisplayName();

                if (displayName.contains("Divan's Drill")) {
                    Chatter.sendDebutChat("Divan drill found at: " + i);
                    mc.thePlayer.inventory.currentItem = i;
                    return i;
                } else if (displayName.contains("Titanium Drill DR-X655")) {
                    Chatter.sendDebutChat("Titanium drill found at: " + i);
                    mc.thePlayer.inventory.currentItem = i;
                    return i;
                } else if (displayName.contains("Gemstone Gauntlet")) {
                    Chatter.sendDebutChat("Gemstone Gauntlet found at: " + i);
                    mc.thePlayer.inventory.currentItem = i;
                    return i;
                } else if (item instanceof ItemPickaxe) {
                    Chatter.sendDebutChat("Pickaxe found in slot: " + i);
                    mc.thePlayer.inventory.currentItem = i;
                    return i;
                }
            }
        }

        return -1;
    }

    public static void updateRotation() {
        if (isRotating) {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - startTime;

            if (elapsedTime < rotationDuration) {
                // Calculate progress of the animation (0.0 to 1.0)
                float progress = (float) elapsedTime / rotationDuration;

                // Easing function for smoother motion
                progress = easeOutCubic(progress);

                // Interpolate between start and target values
                float currentYaw = prevYaw + (targetYaw - prevYaw) * progress;
                float currentPitch = prevPitch + (targetPitch - prevPitch) * progress;

                // Apply current rotation values
                mc.thePlayer.rotationYaw = currentYaw;
                mc.thePlayer.rotationPitch = currentPitch;
            } else {
                // Rotation done, set final values
                mc.thePlayer.rotationYaw = targetYaw;
                mc.thePlayer.rotationPitch = targetPitch;
                isRotating = false;
            }
        }
    }

    // Easing function for more natural motion
    private static float easeOutCubic(float x) {
        return 1 - (float) Math.pow(1 - x, 3);
    }

    // Normalize angle to find shortest rotation path
    private static float normalizeAngle(float angle, float baseAngle) {
        float delta = angle - baseAngle;

        while (delta > 180) {
            delta -= 360;
        }

        while (delta < -180) {
            delta += 360;
        }

        return baseAngle + delta;
    }
}
