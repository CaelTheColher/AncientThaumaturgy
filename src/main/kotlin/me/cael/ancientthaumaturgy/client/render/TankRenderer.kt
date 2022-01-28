package me.cael.ancientthaumaturgy.client.render

import me.cael.ancientthaumaturgy.common.blockentity.TankBlockEntity
import me.cael.ancientthaumaturgy.utils.identifier
import net.fabricmc.fabric.api.renderer.v1.Renderer
import net.fabricmc.fabric.api.renderer.v1.RendererAccess
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.RenderLayers
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.util.SpriteIdentifier
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.screen.PlayerScreenHandler
import net.minecraft.state.property.Properties
import net.minecraft.util.math.Direction
import java.util.*


class TankRenderer : BlockEntityRenderer<TankBlockEntity> {

    override fun render(entity: TankBlockEntity, tickDelta: Float, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int) {
        if (entity.visStorage.amount > 0) {
            drawFluidInTank(
                entity,
                matrices,
                vertexConsumers,
                entity.visStorage.amount.toFloat() / entity.visStorage.capacity.toFloat()
            )
        }
        val world = entity.world
        val blockState = entity.cachedState
        val blockPos = entity.pos
        val blockRenderManager = MinecraftClient.getInstance().blockRenderManager
        blockRenderManager.modelRenderer.render(
            world,
            blockRenderManager.getModel(blockState),
            blockState,
            blockPos,
            matrices,
            vertexConsumers.getBuffer(RenderLayers.getEntityBlockLayer(blockState, false)),
            false,
            Random(),
            blockState.getRenderingSeed(blockPos),
            OverlayTexture.DEFAULT_UV
        )
    }

    private val TANK_W = 0.08f
    private val FULL_LIGHT = 0x00F0_00F0

    fun drawFluidInTank(entity: TankBlockEntity, ms: MatrixStack, vcp: VertexConsumerProvider, amount: Float) {
        var fill = amount
        val spriteIdentifier = SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, identifier("block/yep_vis_and_not_just_recolored_water"))
        val sprite = spriteIdentifier.sprite
        val vc = vcp.getBuffer(RenderLayer.getEntityTranslucent(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE))
        //val color = FluidVariantRendering.getColor(fluid, world, pos)
        //val r = (color shr 16 and 255) / 256f
        //val g = (color shr 8 and 255) / 256f
        //val b = (color and 255) / 256f

        // Make sure fill is within [TANK_W, 1 - TANK_W]
        fill = fill.coerceAtMost(if (entity.cachedState[Properties.UP]) 1f else 0.99f)
        fill = fill.coerceAtLeast(0.01f)
        // Top and bottom positions of the fluid inside the tank
        val topHeight = fill
        val bottomHeight = if (entity.cachedState[Properties.DOWN]) 0f else 0.01f
        val renderer: Renderer = RendererAccess.INSTANCE.renderer!!
        for (direction in Direction.values()) {
            val emitter: QuadEmitter = renderer.meshBuilder().emitter
            if (direction.axis.isVertical) {
                emitter.square(direction, TANK_W, TANK_W, 1 - TANK_W, 1 - TANK_W, if (direction == Direction.UP) 1 - topHeight else bottomHeight)
            } else {
                emitter.square(direction, TANK_W, bottomHeight, 1 - TANK_W, topHeight, TANK_W)
            }
            emitter.spriteBake(0, sprite, MutableQuadView.BAKE_LOCK_UV)
            emitter.spriteColor(0, -1, -1, -1, -1)
            vc.quad(ms.peek(), emitter.toBakedQuad(0, sprite, false), 1f, 1f, 1f, FULL_LIGHT, OverlayTexture.DEFAULT_UV)
        }
    }

}