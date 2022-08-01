package me.cael.ancientthaumaturgy.common.blockentity.sealcombination

import me.cael.ancientthaumaturgy.common.blockentity.SealBlockEntity
import net.minecraft.inventory.Inventory
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction

abstract class AbstractSealCombination(val delay: Int = 1, val range: Double = 0.0, val depth: Double = 0.0) {
    abstract fun tick(seal: SealBlockEntity)

    open fun afterRuneChange(seal: SealBlockEntity) { }
    open fun beforeRuneChange(seal: SealBlockEntity) { }

    fun getArea(pos: BlockPos, direction: Direction): Box {
        val newPos = pos.add(direction.vector)
                .add(range * direction.offsetX, direction.offsetY.toDouble(), range * direction.offsetZ)
        return Box(newPos, newPos).expand(range, range*depth, range)
    }

    fun getLinkedInventory(seal: SealBlockEntity) : Inventory? {
        val linkedPos = seal.linkedInventory ?: return null
        val world = seal.world!!
        val entity = world.getBlockEntity(linkedPos)
        return if (entity is Inventory) {
            entity
        } else {
            seal.linkedInventory = null
            null
        }
    }

}