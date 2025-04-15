package org.polyfrost.skyhelper.mining;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.polyfrost.skyhelper.MacroController;
import org.polyfrost.skyhelper.MainController;
import org.polyfrost.skyhelper.util.Chatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ScanBlocks {
    private static final int SEARCH_RADIUS = 6;
    private static final Random random = new Random();
    private static BlockPos currentTarget = null;
    private static BlockPos lastTargetOrigin = null;
    private static BlockPos lastChosenTarget = null;
    private static long lastTargetChangeTime = 0;
    private static final long TARGET_STABILITY_TIME = 1000;

    static Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        event.getPlayer().addChatMessage(new ChatComponentText("You broke a block: " + event.state.getBlock().getLocalizedName()));
    }

    public static Vec3 getOptimalMiningPoint(BlockPos blockPos) {
        Vec3 playerPos;

        if (MainController.config.isSneakWhileMining())
            playerPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.eyeHeight + 0.2, mc.thePlayer.posZ);
        else
            playerPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.eyeHeight, mc.thePlayer.posZ);

        Vec3 blockCenter = new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);

        double[][] offsets = {
                {0.5, 0.5, 0.5},
                {0.7, 0.5, 0.5},
                {0.2, 0.5, 0.5},
                {0.5, 0.7, 0.5},
                {0.5, 0.2, 0.5},
                {0.5, 0.5, 0.7},
                {0.5, 0.5, 0.2},
                {0.7, 0.7, 0.7},
                {0.2, 0.7, 0.7},
                {0.7, 0.2, 0.7},
                {0.2, 0.2, 0.7},
                {0.7, 0.7, 0.2},
                {0.2, 0.7, 0.2},
                {0.7, 0.2, 0.2},
                {0.2, 0.2, 0.2}
        };

        for (double[] offset : offsets) {
            Vec3 targetPoint = new Vec3(
                    blockPos.getX() + offset[0],
                    blockPos.getY() + offset[1],
                    blockPos.getZ() + offset[2]
            );

            if (hasLineOfSight(playerPos, targetPoint)) {
                return targetPoint;
            }
        }

        return blockCenter;
    }

    private static boolean hasLineOfSight(Vec3 start, Vec3 end) {
        MovingObjectPosition hit = mc.theWorld.rayTraceBlocks(start, end, false, true, false);
        return hit == null || (hit.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK &&
                new BlockPos(end.xCoord, end.yCoord, end.zCoord).equals(hit.getBlockPos()));
    }

    public static BlockPos findClosestBlockToMouse() {
        return findClosestBlockToMouse(MacroController.getIgnoreList());
    }

    public static BlockPos findClosestBlockToMouse(List<BlockPos> ignoredBlocks) {
        MovingObjectPosition mouseRay = mc.thePlayer.rayTrace(5, 1);

        if (lastChosenTarget != null &&
                System.currentTimeMillis() - lastTargetChangeTime < TARGET_STABILITY_TIME &&
                isMinable(lastChosenTarget) < 10 &&
                canReach(lastChosenTarget)) {
            if (!isBlockIgnored(lastChosenTarget, ignoredBlocks)) {
                Block blockAtPos = mc.theWorld.getBlockState(lastChosenTarget).getBlock();
                if (blockAtPos != Blocks.air && blockAtPos != Blocks.bedrock) {
                    return lastChosenTarget;
                }
            } else {
                Chatter.sendDebutChat("Block is on ignore list - skipping");
            }
        }

        if (mouseRay == null || mouseRay.getBlockPos() == null) {
            if (currentTarget != null &&
                    isMinable(currentTarget) < 10 &&
                    canReach(currentTarget) &&
                    !isBlockIgnored(currentTarget, ignoredBlocks)) {
                updateLastChosenTarget(currentTarget);
                return currentTarget;
            }
            currentTarget = null;
            return null;
        }

        BlockPos lookedAtPos = mouseRay.getBlockPos();

        if (isBlockIgnored(lookedAtPos, ignoredBlocks)) {
            BlockPos newTarget = findBestTargetAround(lookedAtPos, ignoredBlocks);
            currentTarget = newTarget;
            updateLastChosenTarget(newTarget);
            return newTarget;
        }

        boolean lookingAtBedrock = mc.theWorld.getBlockState(lookedAtPos).getBlock() == Blocks.bedrock;

        if (!lookingAtBedrock &&
                isMinable(lookedAtPos) < 10 &&
                !isBlockIgnored(lookedAtPos, ignoredBlocks)) {
            currentTarget = lookedAtPos;
            lastTargetOrigin = lookedAtPos;
            updateLastChosenTarget(lookedAtPos);
            return lookedAtPos;
        }

        if (lookedAtPos.equals(lastTargetOrigin)) {
            if (currentTarget != null &&
                    isMinable(currentTarget) < 10 &&
                    canReach(currentTarget) &&
                    !isBlockIgnored(currentTarget, ignoredBlocks)) {
                updateLastChosenTarget(currentTarget);
                return currentTarget;
            }
        }

        lastTargetOrigin = lookedAtPos;
        BlockPos newTarget = findBestTargetAround(lookedAtPos, ignoredBlocks);
        currentTarget = newTarget;
        updateLastChosenTarget(newTarget);

        return newTarget;
    }

    private static boolean isBlockIgnored(BlockPos blockPos, List<BlockPos> ignoredBlocks) {
        return ignoredBlocks != null && !ignoredBlocks.isEmpty() && ignoredBlocks.contains(blockPos);
    }

    private static void updateLastChosenTarget(BlockPos newTarget) {
        if (newTarget != null && (lastChosenTarget == null || !newTarget.equals(lastChosenTarget))) {
            lastChosenTarget = newTarget;
            lastTargetChangeTime = System.currentTimeMillis();
        }
    }

    private static BlockPos findBestTargetAround(BlockPos centerPos, List<BlockPos> ignoredBlocks) {
        List<BlockPos> foundBlocks = new ArrayList<>();

        for (int scanRadius = 1; foundBlocks.isEmpty() && scanRadius < 5; scanRadius++) {
            for (int x = -scanRadius; x <= scanRadius; x++) {
                for (int y = -scanRadius; y <= scanRadius; y++) {
                    for (int z = -scanRadius; z <= scanRadius; z++) {
                        BlockPos checkPos = centerPos.add(x, y, z);
                        if (isMinable(checkPos) < 10 &&
                                canReach(checkPos) &&
                                !isBlockIgnored(checkPos, ignoredBlocks)) {
                            foundBlocks.add(checkPos);
                        }
                    }
                }
            }
        }

        if (foundBlocks.isEmpty()) return null;

        if (random.nextFloat() < 0.2f) {
            return foundBlocks.get(random.nextInt(foundBlocks.size()));
        }

        BlockPos bestBlock = null;
        double closestDistance = Double.MAX_VALUE;

        for (int prio = 1; prio <= 7; prio++) {
            for (BlockPos pos : foundBlocks) {
                if (isMinable(pos) == prio) {
                    double distance = pos.distanceSq(centerPos);
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        bestBlock = pos;
                    }
                }
            }
            if (bestBlock != null) break;
        }

        return bestBlock;
    }

    public static boolean canReach(BlockPos pos) {
        Vec3 eyePos = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
        Vec3 targetPoint = getOptimalMiningPoint(pos);

        Vec3 direction = targetPoint.subtract(eyePos).normalize();
        double reachDistance = MainController.config.isSneakWhileMining() ? 4 : 4.5;

        Vec3 end = eyePos.addVector(
                direction.xCoord * reachDistance,
                direction.yCoord * reachDistance,
                direction.zCoord * reachDistance
        );

        MovingObjectPosition hit = mc.theWorld.rayTraceBlocks(eyePos, end, false, true, false);
        return hit != null && pos.equals(hit.getBlockPos());
    }

    private static int isMinable(BlockPos blockPos) {
        int meta = mc.theWorld.getBlockState(blockPos).getBlock().getMetaFromState(mc.theWorld.getBlockState(blockPos));
        Block block = mc.theWorld.getBlockState(blockPos).getBlock();

        if (block == Blocks.wool && meta == 7) return 1;
        if (block == Blocks.stained_hardened_clay && meta == 9) return 2;
        if (block == Blocks.prismarine && meta == 0) return 3;
        if (block == Blocks.prismarine && meta == 2) return 4;
        if (block == Blocks.prismarine && meta == 1) return 5;
        if (block == Blocks.wool && meta == 3) return 6;
        if (block == Blocks.stone && meta == 4 && MainController.config.isMineTitanium()) return 7;

        return 10;
    }
}
