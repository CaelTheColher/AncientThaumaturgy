package me.cael.ancientthaumaturgy.blocks.seal.combinations

import me.cael.ancientthaumaturgy.blocks.seal.SealBlockEntity
import me.cael.ancientthaumaturgy.utils.forEach
import net.minecraft.inventory.Inventory
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction

abstract class AbstractSealCombination(val delay: Int = 0, val range: Double = 0.0, val depth: Double = 0.0) {
    abstract fun tick(seal: SealBlockEntity)

    fun getArea(pos: BlockPos, direction: Direction): Box {
        val newPos = pos.add(direction.vector)
                .add(range * direction.offsetX, direction.offsetY.toDouble(), range * direction.offsetZ)
        return Box(newPos, newPos).expand(range, range*depth, range)
    }

    fun getLinkedInventory(seal: SealBlockEntity) : Inventory? {
        val area = Box(seal.pos, seal.pos).expand(2.0)
        area.forEach { x, y, z ->
            val pos = BlockPos(x,y,z)
            val world = seal.world!!
            val entity = world.getBlockEntity(pos)
            if (entity is Inventory) return entity
        }
        return null
    }
}