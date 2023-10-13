@file:Suppress("unused")

package me.cael.ancientthaumaturgy.common.item

import me.cael.ancientthaumaturgy.AncientThaumaturgy
import me.cael.ancientthaumaturgy.common.block.BlockCompendium
import me.cael.ancientthaumaturgy.common.item.lexicon.LexiconItem
import me.cael.ancientthaumaturgy.common.item.staff.ExchangeWandItem
import me.cael.ancientthaumaturgy.common.item.staff.PortalWandItem
import me.cael.ancientthaumaturgy.utils.RegistryCompendium
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.item.*
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

    val THAUMIUM_INGOT = register("thaumium_ingot", Item(Item.Settings()))
    val THAUMIUM_SWORD = register("thaumium_sword", SwordItem(ThaumiumToolMaterial(), 3, -2.4F, Item.Settings()))
    val THAUMIUM_PICKAXE = register("thaumium_pickaxe", PickaxeItem(ThaumiumToolMaterial(), 1, -2.8f, Item.Settings()))
    val THAUMIUM_AXE = register("thaumium_axe", AxeItem(ThaumiumToolMaterial(), 6.0f, -3.1f, Item.Settings()))
    val THAUMIUM_SHOVEL = register("thaumium_shovel", ShovelItem(ThaumiumToolMaterial(), 1.5f, -3.0f, Item.Settings()))
    val THAUMIUM_HOE = register("thaumium_hoe", HoeItem(ThaumiumToolMaterial(), -2, -1.0f, Item.Settings()))

    val ENCHANTED_WOOD = register("enchanted_wood", EnchantedWoodItem())

    val PORTAL_WAND = register("portal_wand", PortalWandItem())
    val EXCHANGE_WAND = register("staff_earth", ExchangeWandItem())
    val FIRE_WAND = register("staff_fire", Item(Item.Settings().maxCount(1)))
    val LIGHTNING_WAND = register("staff_air", Item(Item.Settings().maxCount(1)))
    val WATER_WAND = register("staff_water", Item(Item.Settings().maxCount(1)))

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