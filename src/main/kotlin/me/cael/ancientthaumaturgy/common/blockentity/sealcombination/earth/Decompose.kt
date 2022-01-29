package me.cael.ancientthaumaturgy.common.blockentity.sealcombination.earth

import me.cael.ancientthaumaturgy.common.blockentity.SealBlockEntity
import me.cael.ancientthaumaturgy.common.blockentity.sealcombination.AbstractSealCombination
import me.cael.ancientthaumaturgy.utils.forEach
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class Decompose(range: Double, depth: Double) : AbstractSealCombination(10, range, depth) {
    val DECOMPOSE_TABLE = LinkedHashMap<Block, Block>()

    override fun tick(seal: SealBlockEntity) {
        val area = getArea(seal.pos, seal.getDirection())
        DECOMPOSE_TABLE.forEach { (t, _) ->
            area.forEach { x,y,z ->
                val pos = BlockPos(x,y,z)
                val world = seal.world!!
                if (tryDecompose(world, pos, t)) return
            }
        }
    }

    // TODO: Remake this using a fakeplayer to avoid bypassing protections in multiplayer
    private fun tryDecompose(world: World, pos: BlockPos, target: Block) : Boolean {
        val state = world.getBlockState(pos)
        val decomposed = DECOMPOSE_TABLE[state.block] ?: return false
        if (state.block == target && world.setBlockState(pos, decomposed.defaultState)) {
            world.syncWorldEvent(2001, pos, Block.getRawIdFromState(state))
            return true
        }
        return false
    }

    // very temporary while I don't set up a config
    init {
        DECOMPOSE_TABLE[Blocks.COBBLESTONE] = Blocks.GRAVEL
        DECOMPOSE_TABLE[Blocks.GRAVEL] = Blocks.SAND
    }
}