package me.cael.ancientthaumaturgy.common.block

import me.cael.ancientthaumaturgy.common.blockentity.BlockEntityCompendium
import me.cael.ancientthaumaturgy.common.blockentity.CrucibleBlockEntity
import me.cael.ancientthaumaturgy.common.blockentity.MachineEntity
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.ShapeContext
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.function.BooleanBiFunction
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World

class CrucibleBlock(settings: Settings) : BlockWithEntity(settings) {

    companion object {
        val RAY_TRACE_SHAPE: VoxelShape = createCuboidShape(2.0, 4.0, 2.0, 14.0, 16.0, 14.0)
        val OUTLINE_SHAPE: VoxelShape = VoxelShapes.combineAndSimplify(VoxelShapes.fullCube(), VoxelShapes.union(createCuboidShape(0.0, 0.0, 4.0, 16.0, 3.0, 12.0), createCuboidShape(4.0, 0.0, 0.0, 12.0, 3.0, 16.0), createCuboidShape(2.0, 0.0, 2.0, 14.0, 3.0, 14.0), RAY_TRACE_SHAPE), BooleanBiFunction.ONLY_FIRST)
    }

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        if (!world.isClient) {
            if (player.getStackInHand(hand).isEmpty) {
                val blockEntity = world.getBlockEntity(pos) as CrucibleBlockEntity
                val stored = blockEntity.inventory[0]
                if (!stored.isEmpty) {
                    player.inventory.insertStack(stored)
                    blockEntity.setStack(0, ItemStack.EMPTY)
                    blockEntity.markDirtyAndSync()
                    return ActionResult.SUCCESS
                }
            }
        }
        return ActionResult.PASS
    }


    @Suppress("UNCHECKED_CAST")
    override fun <T : BlockEntity?> getTicker(world: World?, state: BlockState?, type: BlockEntityType<T>?): BlockEntityTicker<T>? {
        return checkType(type, BlockEntityCompendium.CRUCIBLE_BLOCK_TYPE,
            MachineEntity.Companion::ticker
        )
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = CrucibleBlockEntity(pos, state)

    override fun getRenderType(state: BlockState): BlockRenderType = BlockRenderType.MODEL

    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape = OUTLINE_SHAPE
    override fun getRaycastShape(state: BlockState?, world: BlockView?, pos: BlockPos?): VoxelShape = RAY_TRACE_SHAPE
}