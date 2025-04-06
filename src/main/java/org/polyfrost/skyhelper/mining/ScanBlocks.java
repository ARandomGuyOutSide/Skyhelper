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
import org.polyfrost.skyhelper.player.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ScanBlocks {
    private static final int SEARCH_RADIUS = 6;
    private static final Random random = new Random();
    private static BlockPos currentTarget = null;
    private static BlockPos lastTargetOrigin = null;

    static Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        event.getPlayer().addChatMessage(new ChatComponentText("Du hast einen Block zerstört: " + event.state.getBlock().getLocalizedName()));
    }

    public static BlockPos findClosestBlockToMouse() {
        MovingObjectPosition mouseRay = mc.thePlayer.rayTrace(5, 1);

        if(mouseRay == null || mouseRay.getBlockPos() == null) {
            if(currentTarget != null && isMinable(currentTarget) < 10 && canReach(currentTarget)) {
                return currentTarget;
            }
            currentTarget = null;
            return null;
        }

        BlockPos lookedAtPos = mouseRay.getBlockPos();
        boolean lookingAtBedrock = mc.theWorld.getBlockState(lookedAtPos).getBlock() == Blocks.bedrock;

        if (!lookingAtBedrock && isMinable(lookedAtPos) < 10) {
            currentTarget = lookedAtPos;
            lastTargetOrigin = lookedAtPos;
            return lookedAtPos;
        }

        if (lookedAtPos.equals(lastTargetOrigin)) {
            if (currentTarget != null && isMinable(currentTarget) < 10 && canReach(currentTarget)) {
                return currentTarget;
            }
        }

        lastTargetOrigin = lookedAtPos;
        currentTarget = findBestTargetAround(lookedAtPos);

        return currentTarget;
    }

    private static BlockPos findBestTargetAround(BlockPos centerPos) {
        int scanRadius = 2;
        List<BlockPos> foundBlocks = new ArrayList<>();

        for (int x = -scanRadius; x <= scanRadius; x++) {
            for (int y = -scanRadius; y <= scanRadius; y++) {
                for (int z = -scanRadius; z <= scanRadius; z++) {
                    BlockPos checkPos = centerPos.add(x, y, z);
                    if (isMinable(checkPos) < 10 && canReach(checkPos)) {
                        foundBlocks.add(checkPos);
                    }
                }
            }
        }

        if(foundBlocks.isEmpty()) return null;

        if(random.nextFloat() < 0.2f) {
            return foundBlocks.get(random.nextInt(foundBlocks.size()));
        }

        BlockPos bestBlock = null;
        double closestDistance = Double.MAX_VALUE;

        for (int prio = 1; prio <= 7; prio++) {
            for(BlockPos pos : foundBlocks) {
                if(isMinable(pos) == prio) {
                    double distance = pos.distanceSq(centerPos);
                    if(distance < closestDistance) {
                        closestDistance = distance;
                        bestBlock = pos;
                    }
                }
            }
            if(bestBlock != null) break;
        }

        return bestBlock;
    }

    public static boolean canReach(BlockPos pos) {
        Minecraft mc = Minecraft.getMinecraft();
        Vec3 eyePos = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
        Vec3 blockCenter = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

        Vec3 direction = blockCenter.subtract(eyePos).normalize();

        double reachDistance = 4.5;
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
        if (block == Blocks.stone && meta == 4) return 7;

        return 10;
    }
}