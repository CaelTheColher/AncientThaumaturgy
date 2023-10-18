package me.cael.ancientthaumaturgy.mixin;

import me.cael.ancientthaumaturgy.common.blockentity.PortableHoleBlockEntity;
import me.cael.ancientthaumaturgy.mixed.MixedBlockEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class MixinAbstractBlockState {

    @Inject(method = "getOutlineShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;", at = @At("HEAD"), cancellable = true)
    public void disableCollisionInPortableHole(BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (world == null || pos == null) return;
        BlockEntity entity = world.getBlockEntity(pos);
        if (entity != null && !(entity instanceof PortableHoleBlockEntity) && ((MixedBlockEntity) entity).ancientThaumaturgy_getPortableHoleTimer() > 0) {
            cir.setReturnValue(VoxelShapes.empty());
        }
    }

}