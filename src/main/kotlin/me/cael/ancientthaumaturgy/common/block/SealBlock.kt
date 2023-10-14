package me.cael.ancientthaumaturgy.common.block

import me.cael.ancientthaumaturgy.common.blockentity.MachineEntity
import me.cael.ancientthaumaturgy.common.blockentity.SealBlockEntity
import me.cael.ancientthaumaturgy.common.item.EssenceItem
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.block.enums.WallMountLocation
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.fluid.FluidState
import net.minecraft.fluid.Fluids
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.tick.OrderedTick

@Suppress("OVERRIDE_DEPRECATION")
class SealBlock : WallMountedBlock(FabricBlockSettings.create().noCollision()), Waterloggable, BlockEntityProvider {

    companion object {
        val WATERLOGGED: BooleanProperty = Properties.WATERLOGGED
        val ENABLED: BooleanProperty = Properties.ENABLED
        val EAST_SHAPE: VoxelShape = createCuboidShape(0.0, 5.5, 5.5, 1.0, 10.5, 10.5)
        val WEST_SHAPE: VoxelShape = createCuboidShape(15.0, 5.5, 5.5, 16.0, 10.5, 10.5)
        val SOUTH_SHAPE: VoxelShape = createCuboidShape(5.5, 5.5, 0.0, 10.5, 10.5, 1.0)
        val NORTH_SHAPE: VoxelShape = createCuboidShape(5.5, 5.5, 15.0, 10.5, 10.5, 16.0)
        val UP_SHAPE: VoxelShape = createCuboidShape(5.5, 0.0, 5.5, 10.5, 1.0, 10.5)
        val DOWN_SHAPE: VoxelShape = createCuboidShape(5.5, 15.0, 5.5, 10.5, 16.0, 10.5)
    }

    init {
        this.defaultState = stateManager.defaultState.with(FACING, Direction.NORTH).with(FACE, WallMountLocation.WALL).with(
            WATERLOGGED, false).with(ENABLED, true)
    }

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        val stack = player.getStackInHand(hand)
        if (stack.item is EssenceItem) {
            if (!world.isClient) {
                val blockEntity = world.getBlockEntity(pos) as SealBlockEntity
                if (blockEntity.runes.length < 3) {
                    blockEntity.beforeRuneChange()
                    blockEntity.runes += (stack.item as EssenceItem).type.id
                    blockEntity.owner = player.uuid
                    blockEntity.afterRuneChange()
                    if (!player.isCreative) stack.decrement(1)
                }
                blockEntity.markDirtyAndSync()
            }
            return ActionResult.CONSUME
        }
        return ActionResult.FAIL
    }

    override fun onBreak(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity) {
        super.onBreak(world, pos, state, player)
        if (!world.isClient) {
            val blockEntity = world.getBlockEntity(pos) as? SealBlockEntity
            blockEntity?.beforeRuneChange()
        }
    }

    override fun onPlaced(world: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, itemStack: ItemStack) {
        if (placer == null || placer !is PlayerEntity || world.isClient) return
        val blockEntity = world.getBlockEntity(pos) as SealBlockEntity
        blockEntity.owner = placer.uuid
        blockEntity.markDirtyAndSync()
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
                if (bl) world.blockTickScheduler.scheduleTick(OrderedTick.create(this, pos))
                else world.setBlockState(pos, state.cycle(ENABLED) as BlockState, 2)
            }
        }
    }

    override fun randomDisplayTick(
        state: BlockState?,
        world: World?,
        pos: BlockPos?,
        random: Random?
    ) {
        super.randomDisplayTick(state, world, pos, random)
    }

    override fun scheduledTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        if(state.get(ENABLED) && world.isReceivingRedstonePower(pos)) world.setBlockState(pos, state.cycle(ENABLED), 2)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(FACING, FACE, WATERLOGGED, ENABLED)
    }

    override fun <T : BlockEntity?> getTicker(world: World, state: BlockState, type: BlockEntityType<T>): BlockEntityTicker<T> {
        return BlockEntityTicker {w, p, s, be -> MachineEntity.ticker(w, p, s, be as SealBlockEntity) }
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = SealBlockEntity(pos, state)

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? = super.getPlacementState(ctx)?.with(
        WATERLOGGED, ctx.world.getFluidState(ctx.blockPos).fluid == Fluids.WATER)?.with(ENABLED, !ctx.world.isReceivingRedstonePower(ctx.blockPos))

    override fun getFluidState(state: BlockState): FluidState = if (state.get(WATERLOGGED)) Fluids.WATER.getStill(false) else Fluids.EMPTY.defaultState
}