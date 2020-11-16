package me.cael.ancientthaumaturgy

import me.cael.ancientthaumaturgy.blocks.seal.BlockRegistry
import me.cael.ancientthaumaturgy.blocks.seal.SealBlockEntity
import me.cael.ancientthaumaturgy.blocks.seal.SealRenderer
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback
import net.minecraft.client.render.RenderLayer
import net.minecraft.screen.PlayerScreenHandler
import net.minecraft.util.Identifier

object AncientThaumaturgyClient : ClientModInitializer {
    override fun onInitializeClient() {
        BlockRegistry.registerBlocksClient()
        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register(ClientSpriteRegistryCallback { _, registry ->
            registry.register(Identifier(AncientThaumaturgy.NAMESPACE, "rune/inner/air"))
            registry.register(Identifier(AncientThaumaturgy.NAMESPACE, "rune/inner/earth"))
            registry.register(Identifier(AncientThaumaturgy.NAMESPACE, "rune/inner/fire"))
            registry.register(Identifier(AncientThaumaturgy.NAMESPACE, "rune/inner/water"))
            registry.register(Identifier(AncientThaumaturgy.NAMESPACE, "rune/inner/magic"))
            registry.register(Identifier(AncientThaumaturgy.NAMESPACE, "rune/inner/corruption"))

            registry.register(Identifier(AncientThaumaturgy.NAMESPACE, "rune/outer/air"))
            registry.register(Identifier(AncientThaumaturgy.NAMESPACE, "rune/outer/earth"))
            registry.register(Identifier(AncientThaumaturgy.NAMESPACE, "rune/outer/fire"))
            registry.register(Identifier(AncientThaumaturgy.NAMESPACE, "rune/outer/water"))
            registry.register(Identifier(AncientThaumaturgy.NAMESPACE, "rune/outer/magic"))
            registry.register(Identifier(AncientThaumaturgy.NAMESPACE, "rune/outer/corruption"))

            registry.register(Identifier(AncientThaumaturgy.NAMESPACE, "rune/center/air"))
            registry.register(Identifier(AncientThaumaturgy.NAMESPACE, "rune/center/earth"))
            registry.register(Identifier(AncientThaumaturgy.NAMESPACE, "rune/center/fire"))
            registry.register(Identifier(AncientThaumaturgy.NAMESPACE, "rune/center/water"))
            registry.register(Identifier(AncientThaumaturgy.NAMESPACE, "rune/center/magic"))
            registry.register(Identifier(AncientThaumaturgy.NAMESPACE, "rune/center/corruption"))

        })
    }
}