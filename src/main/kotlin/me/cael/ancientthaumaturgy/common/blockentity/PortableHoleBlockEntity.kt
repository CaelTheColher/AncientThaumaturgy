package me.cael.ancientthaumaturgy.common.blockentity

import me.cael.ancientthaumaturgy.mixed.MixedBlockEntity
import me.cael.ancientthaumaturgy.network.PacketCompendium.UPDATE_BLOCK_ENTITY
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.PlayerLookup
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtHelper
import net.minecraft.registry.Registries
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class PortableHoleBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(BlockEntityCompendium.PORTABLE_HOLE_BLOCK_TYPE, pos, state) {

    var blockState: BlockState = Blocks.AIR.defaultState
    var forceRenderUpdate = false

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        blockState = NbtHelper.toBlockState(Registries.BLOCK.readOnlyWrapper, nbt.getCompound("BlockState"))
    }

    override fun writeNbt(nbt: NbtCompound) {
        super.writeNbt(nbt)
        nbt.put("BlockState", NbtHelper.fromBlockState(blockState))
    }

    companion object {

        @Suppress("KotlinConstantConditions")
        fun <G: BlockEntity> ticker(world: World, pos: BlockPos, state: BlockState, entity: G) {
            val timer = (entity as MixedBlockEntity).ancientThaumaturgy_getPortableHoleTimer() - 1
            (entity as MixedBlockEntity).ancientThaumaturgy_setPortableHoleTimer(timer)

            val sendUpdatePacket = timer == 0 || (entity is PortableHoleBlockEntity && entity.forceRenderUpdate)

            if (sendUpdatePacket && !world.isClient) {
                PlayerLookup.tracking(entity).forEach { player: ServerPlayerEntity ->
                    ServerPlayNetworking.send(
                        player,
                        UPDATE_BLOCK_ENTITY,
                        PacketByteBufs.create().also {
                            it.writeBlockPos(entity.pos)
                            it.writeInt(timer)
                        }
                    )
                }
            }

            if (timer == 0 && entity is PortableHoleBlockEntity) {
                val newState = entity.blockState
                world.setBlockState(pos, newState)
            }
        }
    }

}