package me.cael.ancientthaumaturgy

import me.cael.ancientthaumaturgy.common.block.BlockCompendium
import me.cael.ancientthaumaturgy.common.blockentity.BlockEntityCompendium
import me.cael.ancientthaumaturgy.common.blockentity.sealcombination.CombinationRegistry
import me.cael.ancientthaumaturgy.common.container.ScreenHandlerCompendium
import me.cael.ancientthaumaturgy.common.entity.EntityCompendium
import me.cael.ancientthaumaturgy.common.item.ItemCompendium
import me.cael.ancientthaumaturgy.common.recipe.CrucibleRecipe
import me.cael.ancientthaumaturgy.common.recipe.InfuserRecipe
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object AncientThaumaturgy : ModInitializer {
    const val NAMESPACE = "ancientthaumaturgy"
    val creativeTab: ItemGroup = FabricItemGroup.builder()
        .displayName(Text.translatable("itemGroup.ancientthaumaturgy.item_group"))
        .icon { ItemStack(ItemCompendium.LEXICON) }
        .build()

    val LOGGER: Logger = LogManager.getLogger("Ancient Thaumaturgy")

    override fun onInitialize() {
        Registry.register(Registries.ITEM_GROUP, Identifier(NAMESPACE, "creative_tab"), creativeTab)
        BlockCompendium.initialize()
        BlockEntityCompendium.initialize()
        ScreenHandlerCompendium.initialize()
        ItemCompendium.initialize()
        CombinationRegistry.registerCombinations()
        EntityCompendium.initialize()
        Registry.register(Registries.RECIPE_SERIALIZER, InfuserRecipe.ID, InfuserRecipe.SERIALIZER)
        Registry.register(Registries.RECIPE_TYPE, InfuserRecipe.ID, InfuserRecipe.TYPE)
        Registry.register(Registries.RECIPE_SERIALIZER, CrucibleRecipe.ID, CrucibleRecipe.SERIALIZER)
        Registry.register(Registries.RECIPE_TYPE, CrucibleRecipe.ID, CrucibleRecipe.TYPE)
    }

}