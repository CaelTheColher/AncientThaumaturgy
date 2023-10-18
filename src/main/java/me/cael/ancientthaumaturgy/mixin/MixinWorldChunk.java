package me.cael.ancientthaumaturgy.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WorldChunk.class)
public class MixinWorldChunk {

    // This concerns me too
    // but its needed so block entities that don't have a ticker will still turn back after being set to the portable hole state.
    // Because this method is only called when a block entity is created, we cannot check if its in the hole state but
    // a possible solution would be to add a check and manually call this method when a block entity enters or exit the state.
    @WrapOperation(
            method = "updateTicker",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;getBlockEntityTicker(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/BlockEntityType;)Lnet/minecraft/block/entity/BlockEntityTicker;"
            )
    )
    public <T extends BlockEntity> BlockEntityTicker<T> injectDummyTicker(BlockState instance, World world, BlockEntityType<T> blockEntityType, Operation<BlockEntityTicker<T>> original, @Local T blockEntity) {
        if (instance.getBlockEntityTicker(world, blockEntity.getType()) == null) {
            return (world1, pos, state, blockEntity1) -> {};
        }
        return original.call(instance, world, blockEntityType);
    }

}
