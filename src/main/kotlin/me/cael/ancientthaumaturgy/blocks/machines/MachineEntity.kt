package me.cael.ancientthaumaturgy.blocks.machines
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.math.BlockPos

open class MachineEntity(type: BlockEntityType<*>?, val storageMax: Int = 0, val inputMax: Int = 0, val outputMax: Int = 0, pos: BlockPos, state: BlockState) : BlockEntity(type, pos, state) {
    val stored: Int = 0

    fun getPressure() = stored * storageMax
}