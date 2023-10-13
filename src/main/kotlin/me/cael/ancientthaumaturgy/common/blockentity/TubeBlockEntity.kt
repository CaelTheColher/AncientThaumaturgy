package me.cael.ancientthaumaturgy.common.blockentity

import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos

class TubeBlockEntity(pos: BlockPos, state: BlockState) : MachineEntity(BlockEntityCompendium.TUBE_BLOCK_TYPE, pos, state, 10, 10, 10)