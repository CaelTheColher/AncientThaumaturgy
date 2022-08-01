/*
 * This class is modified from the Botania mod created by Vazkii
 * Botania Source Code: https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package me.cael.ancientthaumaturgy.client.render

import me.cael.ancientthaumaturgy.AncientThaumaturgy.LOGGER
import me.cael.ancientthaumaturgy.common.item.ItemCompendium
import me.cael.ancientthaumaturgy.common.item.lexicon.ClientTickHandler
import me.cael.ancientthaumaturgy.common.item.lexicon.LexiconItem
import me.cael.ancientthaumaturgy.utils.identifier
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.model.BookModel
import net.minecraft.client.render.entity.model.EntityModelLayers
import net.minecraft.client.util.SpriteIdentifier
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.screen.PlayerScreenHandler
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3f


// Hacky way to render 3D lexicon, will be reevaluated in the future.
// Victor Kohler: yeah... reevaluated. So anyway here is a hacky fabric port.
object RenderLexicon {

    private val model = BookModel(MinecraftClient.getInstance().entityModelLoader.getModelPart(EntityModelLayers.BOOK))
    private val TEXTURE = SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, identifier("model/lexicon"))

    fun renderHand(stack: ItemStack, leftHanded: Boolean, ms: MatrixStack, buffers: VertexConsumerProvider, light: Int) : Boolean {
        if (stack.isEmpty || !stack.isOf(ItemCompendium.LEXICON)) {
            return false
        }
        return try {
            doRender(leftHanded, ms, buffers, light, ClientTickHandler.partialTicks)
            true
        } catch (throwable: Throwable) {
            LOGGER.atError().log("Failed to render lexicon.", throwable)
            false
        }
    }


    private fun doRender(leftHanded: Boolean, ms: MatrixStack, buffers: VertexConsumerProvider, light: Int, partialTicks: Float) {
        ms.push()

        var ticks: Float = ClientTickHandler.ticksWithLexicaOpen
        if (ticks > 0 && ticks < 10) {
            if (LexiconItem.isOpen) {
                ticks += partialTicks
            } else {
                ticks -= partialTicks
            }
        }

        if (!leftHanded) {
            ms.translate((0.3f + 0.02f * ticks).toDouble(), (0.125f + 0.01f * ticks).toDouble(), (-0.2f - 0.035f * ticks).toDouble())
            ms.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180f + ticks * 6))
        } else {
            ms.translate((0.1f - 0.02f * ticks).toDouble(), (0.125f + 0.01f * ticks).toDouble(), (-0.2f - 0.035f * ticks).toDouble())
            ms.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(200f + ticks * 10))
        }
        ms.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-0.3f + ticks * 2.85f))
        val opening = MathHelper.clamp(ticks / 12f, 0f, 1f)

        var pageFlipTicks: Float = ClientTickHandler.pageFlipTicks
        if (pageFlipTicks > 0) {
            pageFlipTicks -= ClientTickHandler.partialTicks
        }

        val pageFlip = pageFlipTicks / 5f

        val leftPageAngle = MathHelper.fractionalPart(pageFlip + 0.25f) * 1.6f - 0.3f
        val rightPageAngle = MathHelper.fractionalPart(pageFlip + 0.75f) * 1.6f - 0.3f
        model.setPageAngles(ClientTickHandler.total, MathHelper.clamp(leftPageAngle, 0.0f, 1.0f), MathHelper.clamp(rightPageAngle, 0.0f, 1.0f), opening)

        val mat = TEXTURE
        val buffer = mat.getVertexConsumer(buffers) { texture: Identifier? -> RenderLayer.getEntitySolid(texture) }
        model.render(ms, buffer, light, OverlayTexture.DEFAULT_UV, 1f, 1f, 1f, 1f)

        if (ticks < 3) {
            val font: TextRenderer = MinecraftClient.getInstance().textRenderer
            ms.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180f))
            ms.translate(-0.252, -0.235, -0.07)
            ms.scale(0.0035f, 0.0035f, -0.0035f)

            val title = "Lexica"
            font.draw(title, 0f, 0f, 0xD69700, false, ms.peek().positionMatrix, buffers, false, 0, light)

            ms.translate(-15.0, 10.0, 0.0)
            val title2 = "Thaumaturga"
            font.draw(title2, 0f, 0f, 0xD69700, false, ms.peek().positionMatrix, buffers, false, 0, light)

            ms.translate(0.0, 10.0, 0.0)
            ms.scale(0.50f, 0.50f, 0.50f)
            val edition: Text = Text.literal("1st Edition").fillStyle(Style.EMPTY.withBold(true).withItalic(true))
            font.draw(edition, 0f, 0f, 0xA07100, false, ms.peek().positionMatrix, buffers, false, 0, light)

            ms.translate(7.5, 190.0, 0.0)
            val authorTitle = "A book by Tector"
            val len: Int = font.getWidth(authorTitle)
            font.draw(authorTitle, 58 - len / 2f, -8f, 0xD69700, false, ms.peek().positionMatrix, buffers, false, 0, light)
        }

        ms.pop()
    }
}