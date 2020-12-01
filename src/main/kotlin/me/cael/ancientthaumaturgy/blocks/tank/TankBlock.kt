package me.cael.ancientthaumaturgy.blocks.tank

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView

class TankBlock(settings: Settings) : Block(settings) {

    companion object {
        val OUTLINE_SHAPE: VoxelShape = createCuboidShape(1.0, 0.0, 1.0, 15.0, 16.0, 15.0)
        val UP: BooleanProperty = Properties.UP
        val DOWN: BooleanProperty = Properties.DOWN
    }

    init {
        this.defaultState = stateManager.defaultState.with(UP, false).with(DOWN, false)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(UP, DOWN)
    }

    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape = OUTLINE_SHAPE
}