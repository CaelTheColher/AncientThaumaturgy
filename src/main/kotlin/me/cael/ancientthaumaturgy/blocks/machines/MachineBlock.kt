package me.cael.ancientthaumaturgy.blocks.machines

import net.minecraft.block.Block
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

// I probably don't need this but we will see
open class MachineBlock(settings: Settings) : Block(settings) {

    fun getNeighbours(world: World, pos: BlockPos) : HashMap<Direction, MachineBlock> {
        val neighbours = HashMap<Direction, MachineBlock>()
        for (direction in Direction.values()) {
            val newPos = pos.offset(direction)
            val block = world.getBlockState(newPos).block ?: continue
            if (block is MachineBlock) neighbours[direction] = block
        }
        return neighbours
    }

}