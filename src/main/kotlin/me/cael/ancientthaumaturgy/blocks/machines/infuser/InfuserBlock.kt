package me.cael.ancientthaumaturgy.blocks.machines.infuser

import me.cael.ancientthaumaturgy.blocks.BlockRegistry
import me.cael.ancientthaumaturgy.utils.BlockScreenHandlerFactory
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class InfuserBlock(settings: Settings) : BlockWithEntity(settings) {

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        return if (world.isClient) {
            ActionResult.SUCCESS
        } else {
            player.openHandledScreen(BlockScreenHandlerFactory(this, pos))
            ActionResult.CONSUME
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : BlockEntity?> getTicker(world: World?, state: BlockState?, type: BlockEntityType<T>?): BlockEntityTicker<T>? {
        return checkType(type, BlockRegistry.getBlockEntity(this) as BlockEntityType<InfuserBlockEntity>, InfuserBlockEntity::ticker)
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = InfuserBlockEntity(BlockRegistry.getBlockEntity(this), pos, state)
    override fun getRenderType(state: BlockState): BlockRenderType = BlockRenderType.MODEL
}