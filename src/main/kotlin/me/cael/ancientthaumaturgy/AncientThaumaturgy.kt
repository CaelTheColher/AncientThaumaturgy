package me.cael.ancientthaumaturgy

import me.cael.ancientthaumaturgy.blocks.BlockRegistry
import me.cael.ancientthaumaturgy.blocks.machines.infuser.InfuserRecipe
import me.cael.ancientthaumaturgy.blocks.seal.combinations.CombinationRegistry
import me.cael.ancientthaumaturgy.items.ItemRegistry
import net.fabricmc.api.EnvType
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.launch.common.FabricLauncherBase
import net.minecraft.util.registry.Registry


object AncientThaumaturgy : ModInitializer {
    const val NAMESPACE = "ancientthaumaturgy"
    val CLIENT = FabricLauncherBase.getLauncher().environmentType == EnvType.CLIENT

    override fun onInitialize() {
        BlockRegistry.registerBlocks()
        ItemRegistry.registerItems()
        CombinationRegistry.registerCombinations()
        Registry.register(Registry.RECIPE_SERIALIZER, InfuserRecipe.ID, InfuserRecipe.SERIALIZER);
        Registry.register(Registry.RECIPE_TYPE, InfuserRecipe.ID, InfuserRecipe.TYPE)
    }

}