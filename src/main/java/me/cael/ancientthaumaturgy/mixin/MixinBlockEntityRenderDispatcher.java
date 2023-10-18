package me.cael.ancientthaumaturgy.mixin;

import me.cael.ancientthaumaturgy.client.render.PortableHoleEntityRenderer;
import me.cael.ancientthaumaturgy.common.blockentity.PortableHoleBlockEntity;
import me.cael.ancientthaumaturgy.mixed.MixedBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntityRenderDispatcher.class)
public abstract class MixinBlockEntityRenderDispatcher {

    @Unique PortableHoleEntityRenderer<BlockEntity> ancientthaumaturgy_portableHoleRenderer = new PortableHoleEntityRenderer<>();

    @Inject(method = "get(Lnet/minecraft/block/entity/BlockEntity;)Lnet/minecraft/client/render/block/entity/BlockEntityRenderer;", at = @At("HEAD"), cancellable = true)
    public void getPortableHoleRenderer(BlockEntity blockEntity, CallbackInfoReturnable<@Nullable BlockEntityRenderer<BlockEntity>> cir) {
        if ( !(blockEntity instanceof PortableHoleBlockEntity) && ((MixedBlockEntity) blockEntity).ancientThaumaturgy_getPortableHoleTimer() > 0) {
            cir.setReturnValue(ancientthaumaturgy_portableHoleRenderer);
        }
    }

}