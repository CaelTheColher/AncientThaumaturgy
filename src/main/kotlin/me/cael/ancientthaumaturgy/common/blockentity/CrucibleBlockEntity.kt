package me.cael.ancientthaumaturgy.common.blockentity

import me.cael.ancientthaumaturgy.vis.api.VisStorage
import me.cael.ancientthaumaturgy.vis.api.VisStorageUtil
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

class CrucibleBlockEntity(pos: BlockPos, state: BlockState) : MachineEntity(BlockEntityCompendium.CRUCIBLE_BLOCK_TYPE, pos, state, 1000, 0, 100) {

    fun tick() {
        visStorage.amount = visStorage.capacity
        val targets = linkedSetOf<VisStorage>()
        Direction.values().forEach { direction ->
            val targetPos = pos.offset(direction)
            VisStorage.SIDED.find(world, targetPos, direction.opposite)?.let { target ->
                if(target.supportsInsertion() && target.amount < target.capacity) {
                    targets.add(target)
                }
            }
        }
        if(targets.size > 0) {
            val transferAmount = visStorage.amount.coerceAtMost(visStorage.maxExtract) / targets.size
            targets.forEach { target ->
                VisStorageUtil.move(visStorage, target, transferAmount, null)
            }
        }
    }

    companion object {
        @Suppress("unused_parameter")
        fun ticker(world: World, pos: BlockPos, state: BlockState, entity: CrucibleBlockEntity) {
            if (!world.isClient) {
                entity.tick()
            }
        }
    }
}