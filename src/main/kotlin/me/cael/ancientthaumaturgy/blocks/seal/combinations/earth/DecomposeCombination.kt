package me.cael.ancientthaumaturgy.blocks.seal.combinations.earth

import me.cael.ancientthaumaturgy.blocks.seal.SealBlockEntity
import me.cael.ancientthaumaturgy.blocks.seal.combinations.AbstractSealCombination
import me.cael.ancientthaumaturgy.utils.forEach
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class DecomposeCombination(range: Double, depth: Double) : AbstractSealCombination(10, range, depth) {
    val DECOMPOSE_MAP = LinkedHashMap<Block, Block>()

    override fun tick(seal: SealBlockEntity) {
        val area = getArea(seal.pos, seal.getDirection())
        DECOMPOSE_MAP.forEach { (t, _) ->
            area.forEach { x,y,z ->
                val pos = BlockPos(x,y,z)
                val world = seal.world!!
                if (tryDecompose(world, pos, t)) return
            }
        }
    }

    private fun tryDecompose(world: World, pos: BlockPos, target: Block) : Boolean {
        val state = world.getBlockState(pos)
        val decomposed = DECOMPOSE_MAP[state.block] ?: return false
        if (state.block == target && world.setBlockState(pos, decomposed.defaultState)) {
            world.syncWorldEvent(2001, pos, Block.getRawIdFromState(state))
            return true
        }
        return false
    }

    // very temporary while I don't set up a config
    init {
        DECOMPOSE_MAP[Blocks.COBBLESTONE] = Blocks.GRAVEL
        DECOMPOSE_MAP[Blocks.GRAVEL] = Blocks.SAND
    }
}