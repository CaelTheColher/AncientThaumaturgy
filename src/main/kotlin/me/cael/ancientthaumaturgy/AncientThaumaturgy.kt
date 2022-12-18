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
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.text.Text
import org.apache.logging.log4j.LogManager


object AncientThaumaturgy : ModInitializer {
    const val NAMESPACE = "ancientthaumaturgy"
    val creativeTab = FabricItemGroup.builder(identifier("item_group"))
        .displayName(Text.translatable("itemGroup.ancientthaumaturgy.item_group"))
        .icon { ItemStack(ItemCompendium.LEXICON) }
        .build()

    val LOGGER = LogManager.getLogger("Ancient Thaumaturgy")

    override fun onInitialize() {
        BlockCompendium.initialize()
        BlockEntityCompendium.initialize()
        ScreenHandlerCompendium.initialize()
        ItemCompendium.initialize()
        CombinationRegistry.registerCombinations()
        Registry.register(Registries.RECIPE_SERIALIZER, InfuserRecipe.ID, InfuserRecipe.SERIALIZER)
        Registry.register(Registries.RECIPE_TYPE, InfuserRecipe.ID, InfuserRecipe.TYPE)
        Registry.register(Registries.RECIPE_SERIALIZER, CrucibleRecipe.ID, CrucibleRecipe.SERIALIZER)
        Registry.register(Registries.RECIPE_TYPE, CrucibleRecipe.ID, CrucibleRecipe.TYPE)

    }

}