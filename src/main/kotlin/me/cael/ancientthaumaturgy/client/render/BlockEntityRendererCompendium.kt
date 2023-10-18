package me.cael.ancientthaumaturgy.client.render

import me.cael.ancientthaumaturgy.common.blockentity.BlockEntityCompendium
import me.cael.ancientthaumaturgy.utils.GenericCompendium
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory

object BlockEntityRendererCompendium: GenericCompendium<BlockEntityRendererFactory<*>>() {
    init {
        register("seal_block", BlockEntityRendererFactory { SealRenderer() })
        register("tank_block", BlockEntityRendererFactory { TankRenderer() })
        register("crucible_block", BlockEntityRendererFactory { CrucibleRenderer() })
        register("portable_hole_block", BlockEntityRendererFactory { PortableHoleEntityRenderer() })
    }

    @Suppress("UNCHECKED_CAST")
    override fun initialize() {
        map.forEach { (entityIdentifier, renderFactory) ->
            BlockEntityRendererRegistry.register(BlockEntityCompendium.get(entityIdentifier) as BlockEntityType<BlockEntity>, renderFactory as BlockEntityRendererFactory<BlockEntity>)
        }
    }
}