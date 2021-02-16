package me.cael.ancientthaumaturgy.blocks.machines.infuser

import me.cael.ancientthaumaturgy.blocks.BlockRegistry
import me.cael.ancientthaumaturgy.utils.BlockScreenHandlerFactory
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
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
    override fun createBlockEntity(world: BlockView?): BlockEntity = InfuserBlockEntity(BlockRegistry.getBlockEntity(this))
    override fun getRenderType(state: BlockState): BlockRenderType = BlockRenderType.MODEL
}