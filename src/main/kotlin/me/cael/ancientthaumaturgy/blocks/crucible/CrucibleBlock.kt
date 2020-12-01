package me.cael.ancientthaumaturgy.blocks.crucible

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.util.function.BooleanBiFunction
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView

class CrucibleBlock(settings: Settings) : Block(settings) {

    companion object {
        val RAY_TRACE_SHAPE: VoxelShape = createCuboidShape(2.0, 4.0, 2.0, 14.0, 16.0, 14.0)
        val OUTLINE_SHAPE: VoxelShape = VoxelShapes.combineAndSimplify(VoxelShapes.fullCube(), VoxelShapes.union(createCuboidShape(0.0, 0.0, 4.0, 16.0, 3.0, 12.0), createCuboidShape(4.0, 0.0, 0.0, 12.0, 3.0, 16.0), createCuboidShape(2.0, 0.0, 2.0, 14.0, 3.0, 14.0), RAY_TRACE_SHAPE), BooleanBiFunction.ONLY_FIRST)
    }

    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape = OUTLINE_SHAPE

    override fun getRaycastShape(state: BlockState?, world: BlockView?, pos: BlockPos?): VoxelShape = RAY_TRACE_SHAPE
}