package me.cael.ancientthaumaturgy.blocks.machines.tank

import me.cael.ancientthaumaturgy.blocks.BlockRegistry
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.ShapeContext
import net.minecraft.block.entity.BlockEntity
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.WorldAccess

class TankBlock(settings: Settings) : BlockWithEntity(settings) {

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

    override fun createBlockEntity(pos: BlockPos?, state: BlockState?): BlockEntity = TankBlockEntity(BlockRegistry.getBlockEntity(this), pos, state)
    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape = OUTLINE_SHAPE
}