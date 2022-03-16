package me.cael.ancientthaumaturgy

import me.cael.ancientthaumaturgy.common.block.BlockCompendium
import me.cael.ancientthaumaturgy.common.blockentity.BlockEntityCompendium
import me.cael.ancientthaumaturgy.common.blockentity.sealcombination.CombinationRegistry
import me.cael.ancientthaumaturgy.common.container.ScreenHandlerCompendium
import me.cael.ancientthaumaturgy.common.item.ItemCompendium
import me.cael.ancientthaumaturgy.common.recipe.CrucibleRecipe
import me.cael.ancientthaumaturgy.common.recipe.InfuserRecipe
import me.cael.ancientthaumaturgy.utils.identifier
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.registry.Registry
import org.apache.logging.log4j.LogManager


object AncientThaumaturgy : ModInitializer {
    const val NAMESPACE = "ancientthaumaturgy"
    private val creativeTab = FabricItemGroupBuilder.build(identifier("item_group")) { ItemStack(BlockCompendium.SEAL_BLOCK.asItem()) }
    val LOGGER = LogManager.getLogger("Ancient Thaumaturgy")

    fun creativeGroupSettings(): Item.Settings = Item.Settings().group(creativeTab)

    override fun onInitialize() {
        BlockCompendium.initialize()
        BlockEntityCompendium.initialize()
        ScreenHandlerCompendium.initialize()
        ItemCompendium.initialize()
        CombinationRegistry.registerCombinations()
        Registry.register(Registry.RECIPE_SERIALIZER, InfuserRecipe.ID, InfuserRecipe.SERIALIZER)
        Registry.register(Registry.RECIPE_TYPE, InfuserRecipe.ID, InfuserRecipe.TYPE)
        Registry.register(Registry.RECIPE_SERIALIZER, CrucibleRecipe.ID, CrucibleRecipe.SERIALIZER)
        Registry.register(Registry.RECIPE_TYPE, CrucibleRecipe.ID, CrucibleRecipe.TYPE)

    }

}