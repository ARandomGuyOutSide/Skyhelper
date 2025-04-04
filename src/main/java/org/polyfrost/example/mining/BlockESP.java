package org.polyfrost.example.mining;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class BlockESP {

    private static List<BlockPos> blockPosList = new ArrayList<>();

    private static BlockPos blockPos;

    public static void setBlockESPOfBlockWithPos(BlockPos blockPos2) {
        blockPos = blockPos2;
    }

    public static void setBlockESPOfBlockWithList(List<BlockPos> list) {
        blockPosList = list;
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (blockPosList == null) return;

        if(blockPos != null)
        {
            Minecraft mc = Minecraft.getMinecraft();
            double x = blockPos.getX() - mc.getRenderManager().viewerPosX;
            double y = blockPos.getY() - mc.getRenderManager().viewerPosY;
            double z = blockPos.getZ() - mc.getRenderManager().viewerPosZ;

            AxisAlignedBB box = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);

            // Füllt die Fläche des Blocks (transparente Box)
            drawFilledBox(box, 1.0f, 0.0f, 0.0f, 0.3f);
        }

        for (BlockPos blockPos : blockPosList) {
            Minecraft mc = Minecraft.getMinecraft();
            double x = blockPos.getX() - mc.getRenderManager().viewerPosX;
            double y = blockPos.getY() - mc.getRenderManager().viewerPosY;
            double z = blockPos.getZ() - mc.getRenderManager().viewerPosZ;

            AxisAlignedBB box = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);

            // Füllt die Fläche des Blocks (transparente Box)
            drawFilledBox(box, 1.0f, 0.0f, 0.0f, 0.3f);
        }
    }


    // Code from MightyMiner v2
    private void drawFilledBox(AxisAlignedBB box, float red, float green, float blue, float alpha) {
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableTexture2D();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        GlStateManager.color(red, green, blue, alpha);

        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(box.minX, box.minY, box.minZ).endVertex();
        worldrenderer.pos(box.maxX, box.minY, box.minZ).endVertex();
        worldrenderer.pos(box.maxX, box.minY, box.maxZ).endVertex();
        worldrenderer.pos(box.minX, box.minY, box.maxZ).endVertex();
        tessellator.draw();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(box.minX, box.maxY, box.maxZ).endVertex();
        worldrenderer.pos(box.maxX, box.maxY, box.maxZ).endVertex();
        worldrenderer.pos(box.maxX, box.maxY, box.minZ).endVertex();
        worldrenderer.pos(box.minX, box.maxY, box.minZ).endVertex();
        tessellator.draw();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(box.minX, box.minY, box.maxZ).endVertex();
        worldrenderer.pos(box.minX, box.maxY, box.maxZ).endVertex();
        worldrenderer.pos(box.minX, box.maxY, box.minZ).endVertex();
        worldrenderer.pos(box.minX, box.minY, box.minZ).endVertex();
        tessellator.draw();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(box.maxX, box.minY, box.minZ).endVertex();
        worldrenderer.pos(box.maxX, box.maxY, box.minZ).endVertex();
        worldrenderer.pos(box.maxX, box.maxY, box.maxZ).endVertex();
        worldrenderer.pos(box.maxX, box.minY, box.maxZ).endVertex();
        tessellator.draw();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(box.minX, box.maxY, box.minZ).endVertex();
        worldrenderer.pos(box.maxX, box.maxY, box.minZ).endVertex();
        worldrenderer.pos(box.maxX, box.minY, box.minZ).endVertex();
        worldrenderer.pos(box.minX, box.minY, box.minZ).endVertex();
        tessellator.draw();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(box.minX, box.minY, box.maxZ).endVertex();
        worldrenderer.pos(box.maxX, box.minY, box.maxZ).endVertex();
        worldrenderer.pos(box.maxX, box.maxY, box.maxZ).endVertex();
        worldrenderer.pos(box.minX, box.maxY, box.maxZ).endVertex();
        tessellator.draw();
        RenderGlobal.drawSelectionBoundingBox(box);
        GL11.glLineWidth(1.0f);
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
