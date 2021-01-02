package me.cael.ancientthaumaturgy.items.lexicon

import me.cael.ancientthaumaturgy.items.ItemRegistry
import net.minecraft.item.Item

class LexiconItem : Item(Settings().group(ItemRegistry.ITEM_GROUP)) {
    companion object {
        fun isOpen(): Boolean {
            return false
        }
    }
}