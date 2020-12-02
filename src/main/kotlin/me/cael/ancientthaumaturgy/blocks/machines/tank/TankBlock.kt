package me.cael.ancientthaumaturgy.blocks.machines.tank

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.WorldAccess

class TankBlock(settings: Settings) : Block(settings) {

    companion object {
        val OUTLINE_SHAPE: VoxelShape = createCuboidShape(1.0, 0.0, 1.0, 15.0, 16.0, 15.0)
        val UP: BooleanProperty = Properties.UP
        val DOWN: BooleanProperty = Properties.DOWN
    }

    init {
        this.defaultState = stateManager.defaultState.with(UP, false).with(DOWN, false)
    }

    override fun getStateForNeighborUpdate(state: BlockState, direction: Direction, newState: BlockState, world: WorldAccess, pos: BlockPos, posFrom: BlockPos): BlockState {
        return when(direction) {
            Direction.UP -> state.with(UP, newState.block is TankBlock)
            Direction.DOWN -> state.with(DOWN, newState.block is TankBlock)
            else -> state
        }
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(UP, DOWN)
    }

    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape = OUTLINE_SHAPE
}