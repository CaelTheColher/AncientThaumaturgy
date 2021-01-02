/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package me.cael.ancientthaumaturgy.items.lexicon

import me.cael.ancientthaumaturgy.items.ItemRegistry
import me.cael.ancientthaumaturgy.mixin.AccessorFirstPersonRenderer
import me.cael.ancientthaumaturgy.utils.identifier
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.options.Perspective
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.model.BookModel
import net.minecraft.client.util.SpriteIdentifier
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.Vector3f
import net.minecraft.screen.PlayerScreenHandler
import net.minecraft.text.Text
import net.minecraft.util.Arm
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Matrix4f
import java.util.*


// Hacky way to render 3D lexicon, will be reevaluated in the future.
// Victor Kohler: yeah... reevaluated. So anyway here is a hacky fabric port.
@Suppress("SameParameterValue")
object RenderLexicon {

    private val model = BookModel()
    private val TEXTURE = SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, identifier("model/lexicon"))

    fun renderHand(tickDelta: Float, hand: Hand, swingProgress: Float, equipProgress: Float, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int) : Boolean {
        val mc = MinecraftClient.getInstance()
        if (mc.options.perspective != Perspective.FIRST_PERSON || mc.player!!.getStackInHand(hand).isEmpty
                || mc.player!!.getStackInHand(hand).item != ItemRegistry.LEXICON) {
            return false
        }
        return try {
            renderFirstPersonItem(mc.player!!, tickDelta, hand, swingProgress, equipProgress, matrices, vertexConsumers, light)
            true
        } catch (throwable: Throwable) {
            println("Failed to render lexicon\n${throwable.message}")
            false
        }
    }

    // [VanillaCopy] FirstPersonRenderer, irrelevant branches stripped out
    private fun renderFirstPersonItem(player: AbstractClientPlayerEntity, partialTicks: Float, hand: Hand, swingProgress: Float, equipProgress: Float, ms: MatrixStack, buffers: VertexConsumerProvider, light: Int) {
        val flag = hand == Hand.MAIN_HAND
        val handside = if (flag) player.mainArm else player.mainArm.opposite
        ms.push()
        val flag3 = handside == Arm.RIGHT
        val f5 = -0.4f * MathHelper.sin(MathHelper.sqrt(swingProgress) * Math.PI.toFloat())
        val f6 = 0.2f * MathHelper.sin(MathHelper.sqrt(swingProgress) * (Math.PI.toFloat() * 2f))
        val f10 = -0.2f * MathHelper.sin(swingProgress * Math.PI.toFloat())
        val l = if (flag3) 1 else -1
        ms.translate((l.toFloat() * f5).toDouble(), f6.toDouble(), f10.toDouble())
        (MinecraftClient.getInstance().heldItemRenderer as AccessorFirstPersonRenderer).ancientthaumaturgy_equipOffset(ms, handside, equipProgress)
        (MinecraftClient.getInstance().heldItemRenderer as AccessorFirstPersonRenderer).ancientthaumaturgy_swingOffset(ms, handside, swingProgress)
        doRender(handside, ms, buffers, light, partialTicks)
        ms.pop()
    }

    private fun doRender(side: Arm, ms: MatrixStack, buffers: VertexConsumerProvider, light: Int, partialTicks: Float) {
        ms.push()

        var ticks: Float = ClientTickHandler.ticksWithLexicaOpen
        if (ticks > 0 && ticks < 10) {
            if (LexiconItem.isOpen()) {
                ticks += partialTicks
            } else {
                ticks -= partialTicks
            }
        }

        if (side == Arm.RIGHT) {
            ms.translate((0.3f + 0.02f * ticks).toDouble(), (0.125f + 0.01f * ticks).toDouble(), (-0.2f - 0.035f * ticks).toDouble())
            ms.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180f + ticks * 6))
        } else {
            ms.translate((0.1f - 0.02f * ticks).toDouble(), (0.125f + 0.01f * ticks).toDouble(), (-0.2f - 0.035f * ticks).toDouble())
            ms.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(200f + ticks * 10))
        }
        ms.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(-0.3f + ticks * 2.85f))
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
        val buffer = mat.getVertexConsumer(buffers, { texture: Identifier? -> RenderLayer.getEntitySolid(texture) })
        model.render(ms, buffer, light, OverlayTexture.DEFAULT_UV, 1f, 1f, 1f, 1f)

        if (ticks < 3) {
            val font: TextRenderer = MinecraftClient.getInstance().textRenderer
            ms.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180f))
            ms.translate(-0.30, -0.24, -0.07)
            ms.scale(0.0023f, 0.0023f, -0.0023f)

            val title = "Lexica Thaumaturga"
            font.draw(font.trimToWidth(title, 100), 0f, 0f, 0xD69700, false, ms.peek().model, buffers, false, 0, light)

            ms.translate(0.0, 10.0, 0.0)
            ms.scale(0.80f, 0.80f, 0.80f)
            val edition: Text = Text.of("69th Edition")
            font.draw(edition, 0f, 0f, 0xA07100, false, ms.peek().model, buffers, false, 0, light)

            val quoteStr = "\"nice\" - me"

            ms.translate(-5.0, 15.0, 0.0)
            renderText(0f, 0f, 140, 0, 0x79ff92, quoteStr, ms.peek().model, buffers, light)

            ms.translate(8.0, 110.0, 0.0)
            val blurb = "Botania is a"
            font.draw(blurb, 0f, 0f, 0x79ff92, false, ms.peek().model, buffers, false, 0, light)

            ms.translate(0.0, 10.0, 0.0)
            val blurb2 = Formatting.UNDERLINE.toString() + "" + Formatting.ITALIC + "tech mod"
            font.draw(blurb2, 0f, 0f, 0x79ff92, false, ms.peek().model, buffers, false, 0, light)

            ms.translate(0.0, -30.0, 0.0)

            val authorTitle = "A book by Vazkii"
            val len: Int = font.getWidth(authorTitle)
            font.draw(authorTitle, 58 - len / 2f, -8f, 0xD69700, false, ms.peek().model, buffers, false, 0, light)
        }

        ms.pop()
    }


    private fun renderText(_x: Float, _y: Float, _width: Int, paragraphSize: Int, color: Int, unlocalizedText: String, matrix: Matrix4f, buffers: VertexConsumerProvider, light: Int) {
        val x = _x+2
        var y = _y+10
        val width = _width-4

        val font: TextRenderer = MinecraftClient.getInstance().textRenderer
        val text: String = unlocalizedText
        val textEntries = text.split("<br>").toTypedArray()

        val lines: MutableList<List<String>> = ArrayList()

        var controlCodes: String
        for (s in textEntries) {
            var words: MutableList<String> = ArrayList()
            var lineStr = ""
            val tokens = s.split(" ").toTypedArray()
            for (token in tokens) {
                val prev = lineStr
                val spaced = "$token "
                lineStr += spaced
                controlCodes = toControlCodes(getControlCodes(prev))
                if (font.getWidth(lineStr) > width) {
                    lines.add(words)
                    lineStr = controlCodes + spaced
                    words = ArrayList()
                }
                words.add(controlCodes + token)
            }
            if (lineStr.isNotEmpty()) {
                lines.add(words)
            }
            lines.add(ArrayList())
        }

        for (words in lines) {
            var xi = x
            val spacing = 4
            for (s in words) {
                val extra = 0
                font.draw(s, xi, y, color, false, matrix, buffers, false, 0, light)
                xi += font.getWidth(s) + spacing + extra
            }
            y += if (words.isEmpty()) paragraphSize else 10
        }
    }

    private fun getControlCodes(s: String): String {
        val controls = s.replace("(?<!\u00a7)(.)".toRegex(), "")
        return controls.replace(".*r".toRegex(), "r")
    }

    private fun toControlCodes(s: String): String {
        return s.replace(".".toRegex(), "\u00a7$0")
    }
}