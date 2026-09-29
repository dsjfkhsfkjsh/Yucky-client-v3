package com.yucky.client.render;

import com.yucky.client.YuckyClient;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;

import java.util.ArrayList;
import java.util.List;

public final class HighlightRenderer {
    private static final List<Entry> entries = new ArrayList<>();
    private static int ticks;
    private HighlightRenderer() {}

    public static void register() {
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.world == null || mc.player == null) return;
            if (++ticks % 10 == 0) refresh(mc);
            render(context.matrixStack(), context.consumers());
        });
    }

    private static void refresh(MinecraftClient mc) {
        entries.clear();
        int radius = Math.min(YuckyClient.CONFIG.renderDistance, 64);
        int yRadius = 48;
        BlockPos o = mc.player.getBlockPos();
        for (int x=o.getX()-radius; x<=o.getX()+radius; x++) {
            for (int z=o.getZ()-radius; z<=o.getZ()+radius; z++) {
                for (int y=Math.max(mc.world.getBottomY(),o.getY()-yRadius); y<=Math.min(mc.world.getTopYInclusive(),o.getY()+yRadius); y++) {
                    BlockPos pos = new BlockPos(x,y,z);
                    BlockState state = mc.world.getBlockState(pos);
                    int color = colorFor(state);
                    if (color != 0) entries.add(new Entry(pos.toImmutable(), color));
                }
            }
        }
    }

    private static void render(MatrixStack matrices, VertexConsumerProvider consumers) {
        if (consumers == null) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        Vec3d camera = mc.gameRenderer.getCamera().getPos();
        VertexConsumer lines = consumers.getBuffer(RenderLayer.getLines());
        for (Entry e : entries) {
            BlockPos p=e.pos;
            double d=p.getSquaredDistance(camera);
            if (d > (double)YuckyClient.CONFIG.renderDistance * YuckyClient.CONFIG.renderDistance) continue;
            BlockState state=mc.world.getBlockState(p);
            VoxelShape shape=state.getOutlineShape(mc.world,p);
            if (shape.isEmpty()) continue;
            float r=((e.color>>16)&255)/255f, g=((e.color>>8)&255)/255f, b=(e.color&255)/255f;
            WorldRenderer.drawShapeOutline(matrices, lines, shape, p.getX()-camera.x, p.getY()-camera.y, p.getZ()-camera.z, r,g,b,YuckyClient.CONFIG.outlineOpacity,true);
        }
    }

    private static int colorFor(BlockState s) {
        if (YuckyClient.CONFIG.storageFinder && (s.isOf(Blocks.CHEST) || s.isOf(Blocks.TRAPPED_CHEST))) return YuckyClient.CONFIG.chestColor;
        if (YuckyClient.CONFIG.storageFinder && s.isOf(Blocks.DROPPER)) return YuckyClient.CONFIG.dropperColor;
        if (YuckyClient.CONFIG.storageFinder && s.isOf(Blocks.DISPENSER)) return YuckyClient.CONFIG.dispenserColor;
        if (YuckyClient.CONFIG.storageFinder && s.isOf(Blocks.BARREL)) return YuckyClient.CONFIG.barrelColor;
        if (YuckyClient.CONFIG.storageFinder && s.isOf(Blocks.SHULKER_BOX)) return YuckyClient.CONFIG.shulkerColor;
        if (YuckyClient.CONFIG.spawnerFinder && s.isOf(Blocks.SPAWNER)) return YuckyClient.CONFIG.spawnerColor;
        return 0;
    }

    private record Entry(BlockPos pos, int color) {}
}
