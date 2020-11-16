package me.cael.ancientthaumaturgy.blocks.seal

import me.cael.ancientthaumaturgy.items.Essence
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.enums.WallMountLocation
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.fluid.FluidState
import net.minecraft.fluid.Fluids
import net.minecraft.item.ItemPlacementContext
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World
import java.util.*

class SealBlock : WallMountedBlock(FabricBlockSettings.of(Material.SUPPORTED).noCollision()), Waterloggable, BlockEntityProvider {

    companion object {
        val WATERLOGGED: BooleanProperty = Properties.WATERLOGGED
        val ENABLED: BooleanProperty = Properties.ENABLED
        val EAST_SHAPE: VoxelShape = createCuboidShape(0.0, 5.5, 5.5, 1.0, 10.5, 10.5)
        val WEST_SHAPE: VoxelShape = createCuboidShape(16.0, 5.5, 5.5, 15.0, 10.5, 10.5)
        val SOUTH_SHAPE: VoxelShape = createCuboidShape(5.5, 5.5, 0.0, 10.5, 10.5, 1.0)
        val NORTH_SHAPE: VoxelShape = createCuboidShape(5.5, 5.5, 16.0, 10.5, 10.5, 15.0)
        val UP_SHAPE: VoxelShape = createCuboidShape(5.5, 0.0, 5.5, 10.5, 1.0, 10.5)
        val DOWN_SHAPE: VoxelShape = createCuboidShape(5.5, 16.0, 5.5, 10.5, 15.0, 10.5)
    }

    init {
        this.defaultState = stateManager.defaultState.with(FACING, Direction.NORTH).with(FACE, WallMountLocation.WALL).with(WATERLOGGED, false).with(ENABLED, true)
    }

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        val stack = player.getStackInHand(hand)
        if (stack.item is Essence) {
            if (!world.isClient) {
                val blockEntity = world.getBlockEntity(pos) as SealBlockEntity
                if (blockEntity.runes.length < 3) {
                    blockEntity.runes += (stack.item as Essence).type.id
                    if (!player.isCreative) stack.decrement(1)
                }
                blockEntity.markDirty()
                blockEntity.sync()
            }
            return ActionResult.CONSUME
        }
        return ActionResult.FAIL
    }

    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape = when (state[FACE]) {
        WallMountLocation.CEILING -> DOWN_SHAPE
        WallMountLocation.FLOOR -> UP_SHAPE
                else -> when (state[FACING]) {
                    Direction.NORTH -> NORTH_SHAPE
                    Direction.SOUTH -> SOUTH_SHAPE
                    Direction.WEST -> WEST_SHAPE
                    Direction.EAST -> EAST_SHAPE
                    else -> NORTH_SHAPE
                }
            }

    override fun neighborUpdate(state: BlockState, world: World, pos: BlockPos, block: Block, fromPos: BlockPos, notify: Boolean) {
        if (!world.isClient) {
            val bl = state.get(ENABLED) as Boolean
            if (bl == world.isReceivingRedstonePower(pos)) {
                if (bl) world.blockTickScheduler.schedule(pos, this, 0)
                else world.setBlockState(pos, state.cycle(ENABLED) as BlockState, 2)
            }
        }
    }

    override fun scheduledTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        if(state.get(ENABLED) && world.isReceivingRedstonePower(pos)) world.setBlockState(pos, state.cycle(ENABLED), 2)
    }

    override fun createBlockEntity(world: BlockView?): BlockEntity = SealBlockEntity(BlockRegistry.getBlockEntity(this))

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? = super.getPlacementState(ctx)?.with(WATERLOGGED, ctx.world.getFluidState(ctx.blockPos).fluid == Fluids.WATER)?.with(ENABLED, !ctx.world.isReceivingRedstonePower(ctx.blockPos))

    override fun getFluidState(state: BlockState): FluidState = if (state.get(WATERLOGGED)) Fluids.WATER.getStill(false) else Fluids.EMPTY.defaultState

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(FACING, FACE, WATERLOGGED, ENABLED)
    }
}