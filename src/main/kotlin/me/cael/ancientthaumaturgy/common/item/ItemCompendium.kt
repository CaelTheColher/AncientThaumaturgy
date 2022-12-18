@file:Suppress("unused")

package me.cael.ancientthaumaturgy.common.item

import me.cael.ancientthaumaturgy.AncientThaumaturgy
import me.cael.ancientthaumaturgy.common.block.BlockCompendium
import me.cael.ancientthaumaturgy.common.item.lexicon.LexiconItem
import me.cael.ancientthaumaturgy.utils.RegistryCompendium
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.item.Item
import net.minecraft.registry.Registries

object ItemCompendium: RegistryCompendium<Item>(Registries.ITEM) {

    val LEXICON = register("lexicon", LexiconItem())

    val ARCANE_TINKERING_TOOL = register("arcane_tinkering_tool", ArcaneTinkeringToolItem())

    val AIR_ESSENCE = register("essence/air", EssenceItem(EssenceItem.Type.AIR))
    val EARTH_ESSENCE = register("essence/earth", EssenceItem(EssenceItem.Type.EARTH))
    val FIRE_ESSENCE = register("essence/fire", EssenceItem(EssenceItem.Type.FIRE))
    val WATER_ESSENCE = register("essence/water", EssenceItem(EssenceItem.Type.WATER))
    val MAGIC_ESSENCE = register("essence/magic", EssenceItem(EssenceItem.Type.MAGIC))
    val CORRUPTION_ESSENCE = register("essence/corruption", EssenceItem(EssenceItem.Type.CORRUPTION))

    val AIR_CRYSTAL = register("crystal/air", Item(Item.Settings()))
    val EARTH_CRYSTAL = register("crystal/earth", Item(Item.Settings()))
    val FIRE_CRYSTAL = register("crystal/fire", Item(Item.Settings()))
    val WATER_CRYSTAL = register("crystal/water", Item(Item.Settings()))
    val MAGIC_CRYSTAL = register("crystal/magic", Item(Item.Settings()))
    val CORRUPTION_CRYSTAL = register("crystal/corruption", Item(Item.Settings()))

    override fun initialize() {
        BlockCompendium.registerBlockItems(map)
        map.forEach {(_, item) ->
            ItemGroupEvents.modifyEntriesEvent(AncientThaumaturgy.creativeTab).register {
                it.add(item)
            }
        }
        super.initialize()
    }
}