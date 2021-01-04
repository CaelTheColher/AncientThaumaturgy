package me.cael.ancientthaumaturgy

import me.cael.ancientthaumaturgy.blocks.BlockRegistry
import me.cael.ancientthaumaturgy.blocks.machines.tube.TubeModel
import me.cael.ancientthaumaturgy.items.lexicon.ClientTickHandler
import me.cael.ancientthaumaturgy.utils.identifier
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry
import net.fabricmc.fabric.api.client.model.ModelVariantProvider
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback
import net.minecraft.screen.PlayerScreenHandler

object AncientThaumaturgyClient : ClientModInitializer {
    override fun onInitializeClient() {
        BlockRegistry.registerBlocksClient()
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

        })
        ModelLoadingRegistry.INSTANCE.registerVariantProvider {
            ModelVariantProvider { modelIdentifier, _ ->
                if (modelIdentifier.namespace == AncientThaumaturgy.NAMESPACE && modelIdentifier.path.contains("tube"))
                    return@ModelVariantProvider TubeModel()
                return@ModelVariantProvider null
            }
        }
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickHandler::clientTickEnd)
    }
}