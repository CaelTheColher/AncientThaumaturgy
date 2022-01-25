package me.cael.ancientthaumaturgy.common.item.lexicon

import me.cael.ancientthaumaturgy.AncientThaumaturgy.creativeGroupSettings
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class LexiconItem : Item(creativeGroupSettings().maxCount(1)) {
    companion object {
        var isOpen = false
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        if (user is ServerPlayerEntity) {
            isOpen = !isOpen
        }
        return TypedActionResult(ActionResult.PASS, user.getStackInHand(hand))
    }
}