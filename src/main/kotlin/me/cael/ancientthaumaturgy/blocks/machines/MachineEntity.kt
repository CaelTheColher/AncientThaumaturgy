package me.cael.ancientthaumaturgy.blocks.machines
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType

open class MachineEntity(type: BlockEntityType<*>?, val storageMax: Int = 0, val inputMax: Int = 0, val outputMax: Int = 0) : BlockEntity(type) {
    val stored: Int = 0

    fun getPressure() = stored * storageMax
}