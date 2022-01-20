package me.cael.ancientthaumaturgy.blocks.machines.tube

import me.cael.ancientthaumaturgy.blocks.machines.MachineEntity
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.math.BlockPos

class TubeBlockEntity(type: BlockEntityType<*>?, pos: BlockPos, state: BlockState) : MachineEntity(type, 100, 100, 100, pos, state)