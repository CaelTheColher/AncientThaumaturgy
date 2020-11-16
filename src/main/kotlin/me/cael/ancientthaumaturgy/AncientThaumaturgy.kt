package me.cael.ancientthaumaturgy

import me.cael.ancientthaumaturgy.blocks.seal.BlockRegistry
import me.cael.ancientthaumaturgy.blocks.seal.combinations.CombinationRegistry
import me.cael.ancientthaumaturgy.items.ItemRegistry
import net.fabricmc.api.EnvType
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.launch.common.FabricLauncherBase
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.screen.ScreenTexts


object AncientThaumaturgy : ModInitializer {
    const val NAMESPACE = "ancientthaumaturgy"
    val CLIENT = FabricLauncherBase.getLauncher().environmentType == EnvType.CLIENT

    override fun onInitialize() {
        BlockRegistry.registerBlocks()
        ItemRegistry.registerItems()
        CombinationRegistry.registerCombinations()
    }

}