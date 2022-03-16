package me.cael.ancientthaumaturgy.client.render

import me.cael.ancientthaumaturgy.common.blockentity.CrucibleBlockEntity
import me.cael.ancientthaumaturgy.utils.identifier
import net.fabricmc.fabric.api.renderer.v1.Renderer
import net.fabricmc.fabric.api.renderer.v1.RendererAccess
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.util.SpriteIdentifier
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.screen.PlayerScreenHandler
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper
import kotlin.random.Random

class CrucibleRenderer : BlockEntityRenderer<CrucibleBlockEntity> {

    override fun render(entity: CrucibleBlockEntity, tickDelta: Float, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int) {
        var amount = 0.3f
        if (entity.visStorage.amount > 0) {
            amount = entity.visStorage.amount.toFloat() / entity.visStorage.capacity.toFloat()
            amount = MathHelper.lerp(tickDelta, entity.lastRenderedAmount, amount)
            drawFluidInTank(matrices, vertexConsumers, amount)
        }
        renderItems(entity, matrices, vertexConsumers, light, overlay, amount)
        entity.lastRenderedAmount = amount
    }

    private val TANK_W = 0.08f
    private val FULL_LIGHT = 0x00F0_00F0

    fun drawFluidInTank(ms: MatrixStack, vcp: VertexConsumerProvider, amount: Float) {
        var fill = amount
        val spriteIdentifier = SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, identifier("block/yep_vis_and_not_just_recolored_water"))
        val sprite = spriteIdentifier.sprite
        val vc = vcp.getBuffer(RenderLayer.getEntityTranslucent(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE))
        //val color = FluidVariantRendering.getColor(fluid, world, pos)
        //val r = (color shr 16 and 255) / 256f
        //val g = (color shr 8 and 255) / 256f
        //val b = (color and 255) / 256f

        // Make sure fill is within [TANK_W, 1 - TANK_W]
        fill = fill.coerceAtMost(0.939f)
        fill = fill.coerceAtLeast(0.26f)

        // Top and bottom positions of the fluid inside the tank
        val topHeight = fill
        val renderer: Renderer = RendererAccess.INSTANCE.renderer!!
        val emitter: QuadEmitter = renderer.meshBuilder().emitter
        emitter.square(Direction.UP, TANK_W, TANK_W, 1 - TANK_W, 1 - TANK_W, 1 - topHeight)
        emitter.spriteBake(0, sprite, MutableQuadView.BAKE_LOCK_UV)
        emitter.spriteColor(0, -1, -1, -1, -1)
        vc.quad(ms.peek(), emitter.toBakedQuad(0, sprite, false), 1f, 1f, 1f, FULL_LIGHT, OverlayTexture.DEFAULT_UV)
    }

    private fun renderItems(entity: CrucibleBlockEntity, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int, amount: Float) {

        var fill = amount / 1.5
        fill = fill.coerceAtLeast(0.3)

        matrices.push()
        matrices.translate(0.5, fill, 0.5)

        val stack = entity.inventory[0]
        val random = Random(entity.hashCode())
        for (i in 1..getRenderedAmount(stack)) {
            matrices.push()
            val x = (random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f
            val y = (random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f
            val z = (random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f
            matrices.translate(x.toDouble(), y.toDouble(), z.toDouble())
            MinecraftClient.getInstance().itemRenderer.renderItem(stack, ModelTransformation.Mode.GROUND, light, overlay, matrices, vertexConsumers, 0)
            matrices.pop()
        }
        matrices.pop()
    }

    private fun getRenderedAmount(stack: ItemStack) : Int {
        val count = stack.count
        return when {
            count > 48 -> 5
            count > 32 -> 4
            count > 16 -> 3
            count > 1 -> 2
            else -> 1
        }
    }

}