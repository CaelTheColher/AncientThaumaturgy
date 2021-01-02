package me.cael.ancientthaumaturgy.items

import net.minecraft.item.Item

class EssenceItem(val type: Type) : Item(Settings().group(ItemRegistry.ITEM_GROUP)) {
    enum class Type(val id: String) {
        AIR("A"), EARTH("E"), FIRE("F"), WATER("W"), MAGIC("M"), CORRUPTION("C");

        companion object {
            fun fromId(id: String): Type {
                values().forEach {
                    if (it.id == id) return it
                }
                return AIR
            }
        }
    }
}

