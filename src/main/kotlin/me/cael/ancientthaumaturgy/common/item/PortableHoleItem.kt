package me.cael.ancientthaumaturgy.common.item

import me.cael.ancientthaumaturgy.common.block.BlockCompendium
import me.cael.ancientthaumaturgy.common.blockentity.PortableHoleBlockEntity
import me.cael.ancientthaumaturgy.mixed.MixedBlockEntity
import me.cael.ancientthaumaturgy.network.PacketCompendium
import me.cael.ancientthaumaturgy.utils.forEach
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.PlayerLookup
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.block.Block
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.item.Item
import net.minecraft.item.ItemUsageContext
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.world.World

class PortableHoleItem : Item(Settings().maxCount(1)) {

    val range = 32

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val world = context.world
        if (world is ClientWorld) return ActionResult.PASS
        val startingPos = context.blockPos
        val side = context.side.opposite
        var endingPos: BlockPos? = null
        for (i in 0..<range) {
            val newPosTop = startingPos.offset(side, i)
            val newPosBottom = if (side.offsetY == 0) newPosTop.withY(newPosTop.y - 1) else newPosTop
            val exitTop = newPosTop.offset(side)
            val exitBottom = newPosBottom.offset(side)
            if (canGoThrough(exitTop, world) && canGoThrough(exitBottom, world)) {
                endingPos = newPosBottom
                break
            }
        }
        if (endingPos == null) {
            context.player?.sendMessage(Text.of("Can't make a hole that deep."), true)
            return ActionResult.FAIL
        }
        val distance = startingPos.getManhattanDistance(endingPos)
        val portableHoleTimer = (distance * 20).coerceAtLeast(40)
        val area = Box(startingPos, endingPos)
        area.forEach{ x,y,z ->
            val pos = BlockPos(x,y,z)
            var blockEntity = world.getBlockEntity(pos)
            val blockState = world.getBlockState(pos)
            if (blockEntity is PortableHoleBlockEntity) return@forEach
            if (blockEntity == null) {
                world.setBlockState(pos, BlockCompendium.PORTABLE_HOLE_BLOCK.defaultState, Block.NOTIFY_ALL or Block.FORCE_STATE)
                blockEntity = world.getBlockEntity(pos)
                if (blockEntity is PortableHoleBlockEntity) {
                    blockEntity.blockState = blockState
                    blockEntity.forceRenderUpdate = true
                }
            }
            (blockEntity as MixedBlockEntity).ancientThaumaturgy_setPortableHoleTimer(portableHoleTimer)
            PlayerLookup.tracking(blockEntity).forEach { player: ServerPlayerEntity ->
                ServerPlayNetworking.send(
                    player,
                    PacketCompendium.UPDATE_BLOCK_ENTITY,
                    PacketByteBufs.create().also {
                        it.writeBlockPos(blockEntity.pos)
                        it.writeInt((blockEntity as MixedBlockEntity).ancientThaumaturgy_getPortableHoleTimer())
                    }
                )
            }
        }
        return ActionResult.SUCCESS
    }

    companion object {
        fun canGoThrough(pos: BlockPos, world: World): Boolean {
            return world.getBlockState(pos).canPathfindThrough(world, pos, NavigationType.LAND)
        }
    }

}