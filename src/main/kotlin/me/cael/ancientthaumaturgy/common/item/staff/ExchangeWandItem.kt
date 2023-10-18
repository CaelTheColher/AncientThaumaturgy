package me.cael.ancientthaumaturgy.common.item.staff

import me.cael.ancientthaumaturgy.utils.getStack
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemUsageContext
import net.minecraft.util.ActionResult

class ExchangeWandItem : Item(Settings().maxCount(1)) {

    private var selected: BlockItem? = null

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        if (context.world.isClient) return ActionResult.PASS
        val pos = context.blockPos
        val state = context.world.getBlockState(pos)
        if (state.hasBlockEntity()) return ActionResult.FAIL

        val player = context.player!!

        if (player.isSneaking) {
            selected = state.block.asItem() as BlockItem
            return ActionResult.SUCCESS
        }

        if(selected == null || player.inventory.count(selected) <= 0 || context.world.getBlockState(pos).block == selected!!.block) return ActionResult.FAIL

        if(!player.isCreative) {
            player.inventory.getStack({ it.item == selected })?.decrement(1)
        }

        // TODO: try to put the item directly in the player's inventory and replace the block directly
        context.world.breakBlock(pos, true, player)
        context.world.setBlockState(pos, selected!!.block.defaultState)
        return ActionResult.CONSUME_PARTIAL
    }

}