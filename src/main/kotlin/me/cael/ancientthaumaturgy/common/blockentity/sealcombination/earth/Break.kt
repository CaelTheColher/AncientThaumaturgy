package me.cael.ancientthaumaturgy.common.blockentity.sealcombination.earth

import me.cael.ancientthaumaturgy.common.blockentity.SealBlockEntity
import me.cael.ancientthaumaturgy.common.blockentity.sealcombination.AbstractSealCombination
import me.cael.ancientthaumaturgy.utils.forEach
import net.minecraft.block.Block
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class Break(range: Double, depth: Double) : AbstractSealCombination(10, range, depth) {
    override fun tick(seal: SealBlockEntity) {
        val area = getArea(seal.pos, seal.getDirection())
        area.forEach{ x,y,z ->
            val pos = BlockPos(x,y,z)
            val world = seal.world!!
            if (tryBreak(world, pos)) return
        }
    }

    private fun tryBreak(world: World, pos: BlockPos) : Boolean {
        val state = world.getBlockState(pos)
        val canBreak = state.getHardness(world, pos) >= 0
        if (canBreak) {
            val broke = world.breakBlock(pos, true)
            if (broke) {
                world.syncWorldEvent(2001, pos, Block.getRawIdFromState(state))
                return true
            }
        }
        return false
    }
}