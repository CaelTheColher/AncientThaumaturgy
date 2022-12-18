package me.cael.ancientthaumaturgy.common.item

import me.cael.ancientthaumaturgy.common.blockentity.SealBlockEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.Item
import net.minecraft.item.ItemUsageContext
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos

class ArcaneTinkeringToolItem : Item(Settings().maxCount(1)) {

    var selected: BlockPos? = null

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        if (context.world.isClient) return ActionResult.PASS
        val pos = context.blockPos
        val entity = context.world.getBlockEntity(pos) ?: return ActionResult.FAIL
        if (entity is SealBlockEntity) {
            if (!context.player!!.isSneaking && selected?.isWithinDistance(entity.pos, 5.0) == true) {
                entity.linkedInventory = selected
            } else {
                entity.linkedInventory = null
            }
            return ActionResult.SUCCESS
        } else if (entity is Inventory) {
            selected = pos
            return ActionResult.SUCCESS
        }
        return ActionResult.PASS
    }




}