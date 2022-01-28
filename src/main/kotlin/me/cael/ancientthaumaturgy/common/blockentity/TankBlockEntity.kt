package me.cael.ancientthaumaturgy.common.blockentity

import me.cael.ancientthaumaturgy.common.block.TankBlock
import me.cael.ancientthaumaturgy.vis.api.EnergyStorage
import me.cael.ancientthaumaturgy.vis.api.EnergyStorageUtil
import me.cael.ancientthaumaturgy.vis.api.base.CombinedEnergyStorage
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class TankBlockEntity(pos: BlockPos, state: BlockState) : MachineEntity(BlockEntityCompendium.TANK_BLOCK_TYPE, pos, state, 2000, 100, 100) {

    fun tick() {
        if (!cachedState[TankBlock.DOWN]) return
        val bellow = world?.getBlockEntity(pos.down()) as? TankBlockEntity ?: return
        val moved = EnergyStorageUtil.move(visStorage, bellow.visStorage, Long.MAX_VALUE, null)
        if (moved > 0) {
            bellow.markDirtyAndSync()
            markDirtyAndSync()
        }
    }

    companion object {
        @Suppress("unused_parameter")
        fun ticker(world: World, pos: BlockPos, state: BlockState, entity: TankBlockEntity) {
            if (!world.isClient) {
                entity.tick()
            }
        }
    }

    class CombinedTankStorage : CombinedEnergyStorage<EnergyStorage>(mutableListOf<EnergyStorage>()) {
        fun add(tank: TankBlockEntity): Boolean {
            parts.add(tank.visStorage)
            return true
        }
    }

}