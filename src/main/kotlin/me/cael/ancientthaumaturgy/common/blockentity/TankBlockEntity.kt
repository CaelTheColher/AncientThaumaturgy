package me.cael.ancientthaumaturgy.common.blockentity

import me.cael.ancientthaumaturgy.common.block.TankBlock
import me.cael.ancientthaumaturgy.vis.api.VisStorage
import me.cael.ancientthaumaturgy.vis.api.VisStorageUtil
import me.cael.ancientthaumaturgy.vis.api.base.CombinedVisStorage
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos

class TankBlockEntity(pos: BlockPos, state: BlockState) : MachineEntity(BlockEntityCompendium.TANK_BLOCK_TYPE, pos, state, 2000, 100, 100) {

    override fun tick() {
        super.tick()
        if (!cachedState[TankBlock.DOWN]) return
        val bellow = world?.getBlockEntity(pos.down()) as? TankBlockEntity ?: return
        val moved = VisStorageUtil.move(visStorage, bellow.visStorage, Long.MAX_VALUE, null)
        if (moved > 0) {
            bellow.markDirtyAndSync()
            markDirtyAndSync()
        }
    }

    class CombinedTankStorage : CombinedVisStorage<VisStorage>(mutableListOf<VisStorage>()) {
        fun add(tank: TankBlockEntity): Boolean {
            parts.add(tank.visStorage)
            return true
        }
    }

}