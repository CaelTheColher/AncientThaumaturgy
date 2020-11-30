package me.cael.ancientthaumaturgy.blocks.seal

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.Property
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView

class TubeBlock(settings: Settings) : Block(settings) {
    override fun getOutlineShape(state: BlockState, view: BlockView?, pos: BlockPos?, context: ShapeContext?): VoxelShape = getShape(state)

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN)
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

    companion object {
        val CENTER_SHAPE: VoxelShape = createCuboidShape(5.0, 5.0, 5.0, 11.0, 11.0, 11.0)
        val DOWN_SHAPE: VoxelShape = createCuboidShape(6.0, 0.0, 6.0, 10.0, 6.0, 10.0)
        val UP_SHAPE: VoxelShape = createCuboidShape(6.0, 10.5, 6.0, 10.0, 16.0, 10.0)
        val SOUTH_SHAPE: VoxelShape = createCuboidShape(6.0, 6.0, 10.5, 10.0, 10.0, 16.0)
        val NORTH_SHAPE: VoxelShape = createCuboidShape(6.0, 6.0, 5.5, 10.0, 10.0, 0.0)
        val EAST_SHAPE: VoxelShape = createCuboidShape(10.5, 6.0, 6.0, 16.0, 10.0, 10.0)
        val WEST_SHAPE: VoxelShape = createCuboidShape(0.0, 6.0, 6.0, 5.5, 10.0, 10.0)

        val NORTH: BooleanProperty = BooleanProperty.of("north")
        val SOUTH: BooleanProperty = BooleanProperty.of("south")
        val EAST: BooleanProperty = BooleanProperty.of("east")
        val WEST: BooleanProperty = BooleanProperty.of("west")
        val UP: BooleanProperty = BooleanProperty.of("up")
        val DOWN: BooleanProperty = BooleanProperty.of("down")

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

        private val SHAPE_CACHE = hashSetOf<TubeShape>()
        private fun getShape(state: BlockState): VoxelShape {
            val directions = Direction.values().filter { dir -> state[getProperty(dir)] }.toTypedArray()
            var tubeShapeCache = SHAPE_CACHE.firstOrNull { shape -> shape.directions.contentEquals(directions) }
            if (tubeShapeCache == null) {
                var shape = CENTER_SHAPE
                Direction.values().forEach { direction ->
                    if (state[getProperty(direction)]) shape = VoxelShapes.union(shape, getShape(direction))
                }
                tubeShapeCache = TubeShape(directions, shape)
                SHAPE_CACHE.add(tubeShapeCache)
            }
            return tubeShapeCache.shape
        }
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
}