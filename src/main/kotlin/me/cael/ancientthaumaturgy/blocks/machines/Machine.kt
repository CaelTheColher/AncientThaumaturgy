package me.cael.ancientthaumaturgy.blocks.machines
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.math.Direction

open class Machine(type: BlockEntityType<*>?) : BlockEntity(type) {

    fun getNeighbours() : HashMap<Direction, Machine> {
        val neighbours = HashMap<Direction, Machine>()
        for (direction in Direction.values()) {
            val pos = this.pos.offset(direction)
            val block = world?.getBlockEntity(pos) ?: continue
            if (block is Machine) neighbours[direction] = block
        }
        return neighbours
    }
}