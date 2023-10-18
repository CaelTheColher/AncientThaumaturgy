package me.cael.ancientthaumaturgy.mixin;

import me.cael.ancientthaumaturgy.common.blockentity.PortableHoleBlockEntity;
import me.cael.ancientthaumaturgy.mixed.MixedBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/world/chunk/WorldChunk$DirectBlockEntityTickInvoker")
public class MixinDirectBlockEntityTickInvoker<T extends BlockEntity>  {

    @Final @Shadow private T blockEntity;

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/entity/BlockEntityTicker;tick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/entity/BlockEntity;)V",
                    shift = At.Shift.AFTER
            )
    )
    public void injectPortableHoleTick(CallbackInfo ci) {
        if (!(blockEntity instanceof PortableHoleBlockEntity) && blockEntity.getWorld() != null && ((MixedBlockEntity) blockEntity).ancientThaumaturgy_getPortableHoleTimer() > 0) {
            PortableHoleBlockEntity.Companion.ticker(blockEntity.getWorld(), blockEntity.getPos(), blockEntity.getCachedState(), blockEntity);
        }
    }

}
