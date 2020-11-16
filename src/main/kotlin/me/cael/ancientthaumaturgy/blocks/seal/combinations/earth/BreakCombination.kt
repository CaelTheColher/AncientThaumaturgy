package me.cael.ancientthaumaturgy.blocks.seal.combinations.earth

import me.cael.ancientthaumaturgy.blocks.seal.SealBlockEntity
import me.cael.ancientthaumaturgy.blocks.seal.combinations.AbstractSealCombination
import me.cael.ancientthaumaturgy.utils.forEach
import net.minecraft.util.math.BlockPos

class BreakCombination(range: Double, depth: Double) : AbstractSealCombination(10, range, depth) {
    override fun tick(seal: SealBlockEntity) {
        val area = getArea(seal.pos, seal.getDirection())
        area.forEach{ x,y,z ->
            val pos = BlockPos(x,y,z)
            val world = seal.world!!
            val state = world.getBlockState(pos)
            val canBreak = state.getHardness(world, pos) >= 0
//            if (canBreak && world.breakBlock(pos, true)) return
        }
    }
}