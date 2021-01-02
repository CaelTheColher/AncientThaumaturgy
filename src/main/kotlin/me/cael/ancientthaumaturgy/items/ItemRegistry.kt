package me.cael.ancientthaumaturgy.items

import me.cael.ancientthaumaturgy.blocks.BlockRegistry
import me.cael.ancientthaumaturgy.items.lexicon.LexiconItem
import me.cael.ancientthaumaturgy.utils.identifier
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.util.registry.Registry

object ItemRegistry {
    class ItemInfo(val id: String, val item: Item) {
        fun register() {
            Registry.register(Registry.ITEM, identifier(id), item)
        }
    }

    val registry = linkedMapOf<Item, ItemInfo>()

    fun register(id: String, item: Item): Item {
        registry[item] = ItemInfo(id, item)
        return item
    }

    val ITEM_GROUP: ItemGroup = FabricItemGroupBuilder.build(identifier("item_group")) { ItemStack(BlockRegistry.SEAL_BLOCK.asItem()) }

    val AIR_ESSENCE = register("essence/air", EssenceItem(EssenceItem.Type.AIR))
    val EARTH_ESSENCE = register("essence/earth", EssenceItem(EssenceItem.Type.EARTH))
    val FIRE_ESSENCE = register("essence/fire", EssenceItem(EssenceItem.Type.FIRE))
    val WATER_ESSENCE = register("essence/water", EssenceItem(EssenceItem.Type.WATER))
    val MAGIC_ESSENCE = register("essence/magic", EssenceItem(EssenceItem.Type.MAGIC))
    val CORRUPTION_ESSENCE = register("essence/corruption", EssenceItem(EssenceItem.Type.CORRUPTION))

    val AIR_CRYSTAL = register("crystal/air", Item(Item.Settings().group(ITEM_GROUP)))
    val EARTH_CRYSTAL = register("crystal/earth", Item(Item.Settings().group(ITEM_GROUP)))
    val FIRE_CRYSTAL = register("crystal/fire", Item(Item.Settings().group(ITEM_GROUP)))
    val WATER_CRYSTAL = register("crystal/water", Item(Item.Settings().group(ITEM_GROUP)))
    val MAGIC_CRYSTAL = register("crystal/magic", Item(Item.Settings().group(ITEM_GROUP)))
    val CORRUPTION_CRYSTAL = register("crystal/corruption", Item(Item.Settings().group(ITEM_GROUP)))

    val LEXICON = register("lexicon", LexiconItem())

    fun registerItems() {
        registry.forEach { it.value.register() }
    }
}