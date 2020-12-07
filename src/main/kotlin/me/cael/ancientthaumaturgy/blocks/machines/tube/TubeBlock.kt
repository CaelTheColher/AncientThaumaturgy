package me.cael.ancientthaumaturgy.blocks.machines.tube

import me.cael.ancientthaumaturgy.blocks.BlockRegistry
import me.cael.ancientthaumaturgy.blocks.machines.Machine
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.block.entity.BlockEntity
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.Properties
import net.minecraft.state.property.Property
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.WorldAccess

class TubeBlock(settings: Settings) : Block(settings), BlockEntityProvider {
    
    companion object {
        val CENTER_SHAPE: VoxelShape = createCuboidShape(5.0, 5.0, 5.0, 11.0, 11.0, 11.0)
        val NORTH_SOUTH_SHAPE: VoxelShape = createCuboidShape(6.0, 6.0, 5.0, 10.0, 10.0, 11.0)
        val EAST_WEST_SHAPE: VoxelShape = createCuboidShape(5.0, 6.0, 6.0, 11.0, 10.0, 10.0)
        val UP_DOWN_SHAPE: VoxelShape = createCuboidShape(6.0, 5.0, 6.0, 10.0, 11.0, 10.0)
        val DOWN_SHAPE: VoxelShape = createCuboidShape(6.0, 0.0, 6.0, 10.0, 6.0, 10.0)
        val UP_SHAPE: VoxelShape = createCuboidShape(6.0, 10.5, 6.0, 10.0, 16.0, 10.0)
        val SOUTH_SHAPE: VoxelShape = createCuboidShape(6.0, 6.0, 10.5, 10.0, 10.0, 16.0)
        val NORTH_SHAPE: VoxelShape = createCuboidShape(6.0, 6.0, 5.5, 10.0, 10.0, 0.0)
        val EAST_SHAPE: VoxelShape = createCuboidShape(10.5, 6.0, 6.0, 16.0, 10.0, 10.0)
        val WEST_SHAPE: VoxelShape = createCuboidShape(0.0, 6.0, 6.0, 5.5, 10.0, 10.0)

        val NORTH: BooleanProperty = Properties.NORTH
        val SOUTH: BooleanProperty = Properties.SOUTH
        val EAST: BooleanProperty = Properties.EAST
        val WEST: BooleanProperty = Properties.WEST
        val UP: BooleanProperty = Properties.UP
        val DOWN: BooleanProperty = Properties.DOWN
    }

    init {
        this.defaultState = stateManager.defaultState
                .with(NORTH, false)
                .with(SOUTH, false)
                .with(EAST, false)
                .with(WEST, false)
                .with(UP, false)
                .with(DOWN, false)
    }

    override fun getStateForNeighborUpdate(state: BlockState, direction: Direction, newState: BlockState, world: WorldAccess, pos: BlockPos, posFrom: BlockPos): BlockState {
        val neighbourBlockEntity = world.getBlockEntity(posFrom)
        return state.with(getProperty(direction), neighbourBlockEntity is Machine)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN)
    }

    override fun createBlockEntity(world: BlockView?): BlockEntity = TubeBlockEntity(BlockRegistry.getBlockEntity(this))
    override fun getOutlineShape(state: BlockState, view: BlockView?, pos: BlockPos?, context: ShapeContext?): VoxelShape = getShape(state)

    fun getProperty(facing: Direction): Property<Boolean> {
        return when (facing) {
            Direction.EAST -> EAST
            Direction.WEST -> WEST
            Direction.NORTH -> NORTH
            Direction.SOUTH -> SOUTH
            Direction.UP -> UP
            Direction.DOWN -> DOWN
            else -> EAST
        }
    }

    fun getShape(direction: Direction): VoxelShape {
        var shape = VoxelShapes.empty()
        if (direction == Direction.NORTH) shape = NORTH_SHAPE
        if (direction == Direction.SOUTH) shape = SOUTH_SHAPE
        if (direction == Direction.EAST) shape = EAST_SHAPE
        if (direction == Direction.WEST) shape = WEST_SHAPE
        if (direction == Direction.UP) shape = UP_SHAPE
        if (direction == Direction.DOWN) shape = DOWN_SHAPE
        return shape
    }

    private val SHAPE_CACHE = hashSetOf<TubeShape>()
    private fun getShape(state: BlockState): VoxelShape {
        val directions = Direction.values().filter { dir -> state[getProperty(dir)] }.toTypedArray()
        var tubeShapeCache = SHAPE_CACHE.firstOrNull { shape -> shape.directions.contentEquals(directions) }
        if (tubeShapeCache == null) {
            var shape = run {
                // I still hate this
                if (state[NORTH] && state[SOUTH] && !(state[EAST] || state[WEST] || state[UP] || state[DOWN])) {
                    NORTH_SOUTH_SHAPE
                } else if (state[EAST] && state[WEST] && !(state[NORTH] || state[SOUTH] || state[UP] || state[DOWN])) {
                    EAST_WEST_SHAPE
                } else if (state[UP] && state[DOWN] && !(state[NORTH] || state[SOUTH] || state[EAST] || state[WEST])) {
                    UP_DOWN_SHAPE
                } else {
                    CENTER_SHAPE
                }
            }
            Direction.values().forEach { direction ->
                if (state[getProperty(direction)]) shape = VoxelShapes.union(shape, getShape(direction))
            }
            tubeShapeCache = TubeShape(directions, shape)
            SHAPE_CACHE.add(tubeShapeCache)
        }
        return tubeShapeCache.shape
    }

    data class TubeShape(val directions: Array<Direction>, val shape: VoxelShape) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as TubeShape

            if (!directions.contentEquals(other.directions)) return false
            if (shape != other.shape) return false

            return true
        }

        override fun hashCode(): Int {
            var result = directions.contentHashCode()
            result = 31 * result + shape.hashCode()
            return result
        }
    }
}