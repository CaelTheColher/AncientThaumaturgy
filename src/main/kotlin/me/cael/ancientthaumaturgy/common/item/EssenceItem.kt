package me.cael.ancientthaumaturgy.common.item

import me.cael.ancientthaumaturgy.AncientThaumaturgy.creativeGroupSettings
import net.minecraft.item.Item

class EssenceItem(val type: Type) : Item(creativeGroupSettings()) {
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

