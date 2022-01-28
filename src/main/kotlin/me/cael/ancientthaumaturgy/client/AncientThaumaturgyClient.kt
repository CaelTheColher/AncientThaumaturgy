package me.cael.ancientthaumaturgy.client

import me.cael.ancientthaumaturgy.AncientThaumaturgy
import me.cael.ancientthaumaturgy.client.model.TubeModel
import me.cael.ancientthaumaturgy.client.render.BlockEntityRendererCompendium
import me.cael.ancientthaumaturgy.common.block.BlockCompendium
import me.cael.ancientthaumaturgy.common.container.ScreenHandlerCompendium
import me.cael.ancientthaumaturgy.common.item.lexicon.ClientTickHandler
import me.cael.ancientthaumaturgy.network.PacketCompendium
import me.cael.ancientthaumaturgy.utils.identifier
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry
import net.fabricmc.fabric.api.client.model.ModelVariantProvider
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback
import net.minecraft.client.render.RenderLayer
import net.minecraft.screen.PlayerScreenHandler

object AncientThaumaturgyClient : ClientModInitializer {
    override fun onInitializeClient() {
        ScreenHandlerCompendium.onInitializeClient()
        BlockEntityRendererCompendium.initialize()
        PacketCompendium.onInitializeClient()
        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register(ClientSpriteRegistryCallback { _, registry ->
            registry.register(identifier("rune/inner/air"))
            registry.register(identifier("rune/inner/earth"))
            registry.register(identifier("rune/inner/fire"))
            registry.register(identifier("rune/inner/water"))
            registry.register(identifier("rune/inner/magic"))
            registry.register(identifier("rune/inner/corruption"))

            registry.register(identifier("rune/outer/air"))
            registry.register(identifier("rune/outer/earth"))
            registry.register(identifier("rune/outer/fire"))
            registry.register(identifier("rune/outer/water"))
            registry.register(identifier("rune/outer/magic"))
            registry.register(identifier("rune/outer/corruption"))

            registry.register(identifier("rune/center/air"))
            registry.register(identifier("rune/center/earth"))
            registry.register(identifier("rune/center/fire"))
            registry.register(identifier("rune/center/water"))
            registry.register(identifier("rune/center/magic"))
            registry.register(identifier("rune/center/corruption"))

            registry.register(identifier("model/lexicon"))

            registry.register(identifier("block/yep_vis_and_not_just_recolored_water"))

        })
        ModelLoadingRegistry.INSTANCE.registerVariantProvider {
            ModelVariantProvider { modelIdentifier, _ ->
                if (modelIdentifier.namespace == AncientThaumaturgy.NAMESPACE && modelIdentifier.path.contains("tube"))
                    return@ModelVariantProvider TubeModel()
                return@ModelVariantProvider null
            }
        }

        ClientTickEvents.END_CLIENT_TICK.register(ClientTickHandler::clientTickEnd)
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTranslucent(), BlockCompendium.TANK_BLOCK, BlockCompendium.TUBE_BLOCK)
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockCompendium.CRUCIBLE_BLOCK, BlockCompendium.SEAL_BLOCK, BlockCompendium.INFUSER_BLOCK, BlockCompendium.AIR_CRYSTAL_CLUSTER, BlockCompendium.EARTH_CRYSTAL_CLUSTER, BlockCompendium.FIRE_CRYSTAL_CLUSTER, BlockCompendium.WATER_CRYSTAL_CLUSTER, BlockCompendium.MAGIC_CRYSTAL_CLUSTER, BlockCompendium.CORRUPTION_CRISTAL_CLUSTER)
    }
}