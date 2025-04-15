package org.polyfrost.skyhelper;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.polyfrost.skyhelper.mining.BlockESP;
import org.polyfrost.skyhelper.mining.ScanBlocks;
import org.polyfrost.skyhelper.player.Player;
import org.polyfrost.skyhelper.util.Chatter;
import org.polyfrost.skyhelper.util.InventoryStuff;
import org.polyfrost.skyhelper.util.Timer;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.polyfrost.skyhelper.MacroController.SetupState.GET_MINING_TOOL;

public class MacroController {
    private static double delayTicks = 0;
    private static BlockPos lastTargetBlock = null;
    private static boolean positionReached = false;
    private static int miningFailsafeCounter = 0;
    private static int maxMiningTime;
    private static boolean miningAbilityReady = true;

    private static List<BlockPos> ignoreList = new LinkedList<>();

    public enum SetupState {
        WARP_TO_FORGE,
        GET_MINING_TOOL,
        OPEN_SKYBLOCK_MENU,
        NONE
    }

    public enum MiningState {
        SEARCH_FOR_BLOCK,
        MINE_BLOCK,
        GET_MINING_TOOL,
        NONE
    }

    private static SetupState setupState = SetupState.NONE;
    private static MiningState miningState = MiningState.NONE;

    private static Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        // Update rotation every tick
        Player.updateRotation();

        // Handle delay
        if (delayTicks > 0) {
            delayTicks--;
            return;
        }

        // Setup state handling
        switch (setupState) {
            case WARP_TO_FORGE:
                Chatter.sendChatMessageToUser("Macro status changed to §aenabled§r");
                Player.warpToForge();
                setupState = GET_MINING_TOOL;
                Chatter.sendDebutChat("Waiting three seconds");
                delayTicks = Timer.secondsToTicks(3);
                break;

            case GET_MINING_TOOL:
                Player.getMiningTool();
                setupState = SetupState.OPEN_SKYBLOCK_MENU;
                delayTicks = Timer.secondsToTicks(1);
                break;

            case OPEN_SKYBLOCK_MENU:
                Player.openSkyblockMenu();
                delayTicks = Timer.secondsToTicks(1);
                InventoryStuff.clickOpenContainerSlot(44);
                setupState = SetupState.NONE;
                break;
        }

        // Mining state handling
        switch (miningState) {
            case GET_MINING_TOOL:
                Player.getMiningTool();
                miningState = MiningState.SEARCH_FOR_BLOCK;
                delayTicks = Timer.secondsToTicks(0.5);
                break;

            case SEARCH_FOR_BLOCK:
                try {
                    // Highlight ignored blocks if enabled
                    for (BlockPos ignored : ignoreList) {
                        if (MainController.config.isShowIgnoredBlocks())
                            mc.theWorld.setBlockState(ignored, Blocks.redstone_block.getDefaultState());
                    }

                    BlockPos targetBlock = ScanBlocks.findClosestBlockToMouse();

                    if (targetBlock != null) {
                        lastTargetBlock = targetBlock;
                        BlockESP.setSingleBlockESP(targetBlock);

                        Vec3 optimalPoint = ScanBlocks.getOptimalMiningPoint(targetBlock);
                        Player.smoothRotateToBlock(optimalPoint.xCoord, optimalPoint.yCoord, optimalPoint.zCoord);

                        miningFailsafeCounter++;

                        if (miningFailsafeCounter >= maxMiningTime) {
                            addToIgnoreList();
                            miningFailsafeCounter = 0;
                        }

                        if (isLookingDirectlyAt(targetBlock)) {
                            miningState = MiningState.MINE_BLOCK;
                            miningFailsafeCounter = 0;
                            Chatter.sendDebutChat("§aStarted mining block at position " + targetBlock.toString());
                        }
                    } else {
                        Chatter.sendDebutChat("§cNo mineable block found.");
                        delayTicks = Timer.secondsToTicks(1);
                        Player.moveWhileMining();
                    }

                } catch (Exception e) {
                    Chatter.sendDebutChat("§cBlock search error: " + e.getMessage());
                    delayTicks = Timer.secondsToTicks(1);
                }
                break;

            case MINE_BLOCK:
                if (miningAbilityReady && MainController.config.isUseSpeedBoost()) {
                    miningAbilityReady = Player.useMiningSpeedBoost();
                }

                if (lastTargetBlock != null) {
                    Block blockAtPos = mc.theWorld.getBlockState(lastTargetBlock).getBlock();
                    miningFailsafeCounter++;

                    if (MainController.config.isSneakWhileMining()) {
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
                    }

                    if (miningFailsafeCounter % 10 == 0) {
                        Chatter.sendDebutChat("§7Mining timer: " + miningFailsafeCounter + "/" + maxMiningTime);
                    }

                    boolean isTitaniumButIgnored = blockAtPos == Blocks.stone &&
                            mc.theWorld.getBlockState(lastTargetBlock).getBlock().getMetaFromState(
                                    mc.theWorld.getBlockState(lastTargetBlock)
                            ) == 4 &&
                            !MainController.config.isMineTitanium();

                    if (blockAtPos == Blocks.air ||
                            blockAtPos == Blocks.bedrock ||
                            isTitaniumButIgnored ||
                            miningFailsafeCounter >= maxMiningTime) {

                        if (miningFailsafeCounter >= maxMiningTime) {
                            addToIgnoreList();
                        } else if (blockAtPos == Blocks.bedrock) {
                            Chatter.sendDebutChat("§cBedrock detected. Searching for new block.");
                            if (Math.random() < 0.05) {
                                Player.moveWhileMining();
                            }
                        } else {
                            Chatter.sendDebutChat("§aBlock successfully mined.");
                            if (Math.random() < 0.2) {
                                Player.moveWhileMining();
                            }
                        }

                        miningFailsafeCounter = 0;
                        delayTicks = Timer.secondsToTicks(getRandomizedDelay(MainController.config.getWaitForSeconds()) / 100.0);
                        miningState = MiningState.SEARCH_FOR_BLOCK;
                    } else {
                        Vec3 optimalPoint = ScanBlocks.getOptimalMiningPoint(lastTargetBlock);
                        Player.smoothRotateToBlock(optimalPoint.xCoord, optimalPoint.yCoord, optimalPoint.zCoord);
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), true);
                    }
                } else {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
                    if (MainController.config.isSneakWhileMining()) {
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
                    }
                    miningState = MiningState.SEARCH_FOR_BLOCK;
                }
                break;
        }
    }

    private void addToIgnoreList() {
        Chatter.sendDebutChat("§cMining timed out after " + miningFailsafeCounter + " ticks. Searching for a new block.");
        ignoreList.add(lastTargetBlock);
        Chatter.sendDebutChat("Added block to ignore list");
    }

    public static List<BlockPos> getIgnoreList() {
        return ignoreList;
    }

    private int getRandomizedDelay(int baseDelay) {
        int variation = (int) (baseDelay * (MainController.config.getMiningDelayVariation() / 100.0));
        return baseDelay + (new Random().nextInt(variation * 2) - variation);
    }

    private boolean isLookingDirectlyAt(BlockPos blockPos) {
        MovingObjectPosition ray = mc.thePlayer.rayTrace(6, 1);
        return ray != null && ray.getBlockPos() != null && ray.getBlockPos().equals(blockPos);
    }

    public static void setIgnoreList(List<BlockPos> ignoreList) {
        MacroController.ignoreList = ignoreList;
    }

    public static void setMaxMiningTime(int maxMiningTime) {
        MacroController.maxMiningTime = maxMiningTime;
    }

    public static void setState(SetupState state) {
        setupState = state;
    }

    public static void setState(MiningState state) {
        miningState = state;
    }
}
