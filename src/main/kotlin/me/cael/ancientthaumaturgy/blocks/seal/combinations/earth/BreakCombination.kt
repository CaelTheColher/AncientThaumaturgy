package me.cael.ancientthaumaturgy.blocks.seal.combinations.earth

import me.cael.ancientthaumaturgy.blocks.seal.SealBlockEntity
import me.cael.ancientthaumaturgy.blocks.seal.combinations.AbstractSealCombination
import me.cael.ancientthaumaturgy.utils.forEach
import net.minecraft.block.Block
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class BreakCombination(range: Double, depth: Double) : AbstractSealCombination(10, range, depth) {
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