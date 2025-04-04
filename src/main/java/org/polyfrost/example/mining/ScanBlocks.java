package org.polyfrost.example.mining;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.polyfrost.example.player.Player;

import java.util.ArrayList;
import java.util.List;

public class ScanBlocks {
    private List<BlockPos> blockPosList = new ArrayList<>();

    static Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        event.getPlayer().addChatMessage(new ChatComponentText("Du hast einen Block zerstört: " + event.state.getBlock().getLocalizedName()));
    }

    public static List<BlockPos> scanBlockAroundPlayer() {
        BlockPos playerPos = Player.getPlayerPos();
        List<BlockPos> blocksList = new ArrayList<>();

        for (int x = playerPos.getX() - 5; x < playerPos.getX() + 5; x++) {
            for (int y = playerPos.getY() - 4; y < playerPos.getY() + 4; y++) {
                for (int z = playerPos.getZ() - 5; z < playerPos.getZ() + 5; z++) {
                    BlockPos blockPos = new BlockPos(x, y, z);
                    IBlockState state = mc.theWorld.getBlockState(blockPos);
                    Block block = state.getBlock();
                    int meta = block.getMetaFromState(state);

                    if (isMinable(block, meta)) {
                        blocksList.add(blockPos);
                    }
                }
            }
        }

        return blocksList;
    }

    private static boolean isMinable(Block block, int meta) {
        return (block == Blocks.wool && (meta == 7 || meta == 3)) || // Graue & Hellblaue Wolle
                (block == Blocks.stained_hardened_clay && meta == 9) || // Cyan gehärteter Ton
                (block == Blocks.prismarine && (meta == 2 || meta == 1)) || // Prismarin & Dunkles Prismarin
                (block == Blocks.stone && meta == 4); // Polierter Diorit
    }


}
