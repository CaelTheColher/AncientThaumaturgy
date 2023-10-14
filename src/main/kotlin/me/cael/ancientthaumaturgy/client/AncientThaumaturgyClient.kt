package me.cael.ancientthaumaturgy.client

import me.cael.ancientthaumaturgy.AncientThaumaturgy
import me.cael.ancientthaumaturgy.client.model.TubeModel
import me.cael.ancientthaumaturgy.client.render.BlockEntityRendererCompendium
import me.cael.ancientthaumaturgy.client.render.FireParticleEntityRenderer
import me.cael.ancientthaumaturgy.common.block.BlockCompendium
import me.cael.ancientthaumaturgy.common.container.ScreenHandlerCompendium
import me.cael.ancientthaumaturgy.common.entity.EntityCompendium
import me.cael.ancientthaumaturgy.common.item.lexicon.ClientTickHandler
import me.cael.ancientthaumaturgy.network.PacketCompendium
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry
import net.fabricmc.fabric.api.client.model.ModelVariantProvider
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.entity.EntityRendererFactory

object AncientThaumaturgyClient : ClientModInitializer {
    override fun onInitializeClient() {
        ScreenHandlerCompendium.onInitializeClient()
        BlockEntityRendererCompendium.initialize()
        PacketCompendium.onInitializeClient()

        ModelLoadingRegistry.INSTANCE.registerVariantProvider {
            ModelVariantProvider { modelIdentifier, _ ->
                if (modelIdentifier.namespace == AncientThaumaturgy.NAMESPACE && modelIdentifier.path.contains("tube"))
                    return@ModelVariantProvider TubeModel()
                return@ModelVariantProvider null
            }
        }

        ClientTickEvents.END_CLIENT_TICK.register(ClientTickHandler::clientTickEnd)
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTranslucent(), BlockCompendium.TANK_BLOCK, BlockCompendium.TUBE_BLOCK)
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockCompendium.CRUCIBLE_BLOCK, BlockCompendium.SEAL_BLOCK, BlockCompendium.INFUSER_BLOCK, BlockCompendium.AIR_CRYSTAL_CLUSTER, BlockCompendium.EARTH_CRYSTAL_CLUSTER, BlockCompendium.FIRE_CRYSTAL_CLUSTER, BlockCompendium.WATER_CRYSTAL_CLUSTER, BlockCompendium.MAGIC_CRYSTAL_CLUSTER, BlockCompendium.CORRUPTION_CRYSTAL_CLUSTER)
        EntityRendererRegistry.register(EntityCompendium.FIRE_PARTICLE) { context: EntityRendererFactory.Context -> FireParticleEntityRenderer(context) }
    }
}