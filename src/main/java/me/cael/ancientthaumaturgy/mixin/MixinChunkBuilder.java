package me.cael.ancientthaumaturgy.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.cael.ancientthaumaturgy.common.blockentity.PortableHoleBlockEntity;
import me.cael.ancientthaumaturgy.mixed.MixedBlockEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net/minecraft/client/render/chunk/ChunkBuilder$BuiltChunk$RebuildTask")
public class MixinChunkBuilder {

    @ModifyExpressionValue(
            method = "render(FFFLnet/minecraft/client/render/chunk/BlockBufferBuilderStorage;)Lnet/minecraft/client/render/chunk/ChunkBuilder$BuiltChunk$RebuildTask$RenderData;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getRenderType()Lnet/minecraft/block/BlockRenderType;")
    )
    private BlockRenderType disableRenderInPortableHole(BlockRenderType getRenderType, @Local(ordinal = 2) BlockPos blockPos, @Local ChunkRendererRegion chunkRendererRegion) {
        BlockEntity entity = chunkRendererRegion.getBlockEntity(blockPos);
        if (entity != null && !(entity instanceof PortableHoleBlockEntity) && ((MixedBlockEntity) entity).ancientThaumaturgy_getPortableHoleTimer() > 0) {
            return BlockRenderType.INVISIBLE;
        } else {
            return getRenderType;
        }
    }

}