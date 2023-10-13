package me.cael.ancientthaumaturgy.common.item

import net.minecraft.item.Item
import net.minecraft.item.ItemStack

class EnchantedWoodItem : Item(Settings()) {
    override fun hasGlint(stack: ItemStack): Boolean = true
}