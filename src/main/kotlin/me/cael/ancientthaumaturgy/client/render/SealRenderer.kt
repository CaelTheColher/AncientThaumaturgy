package me.cael.ancientthaumaturgy.client.render

import me.cael.ancientthaumaturgy.common.blockentity.SealBlockEntity
import me.cael.ancientthaumaturgy.common.item.EssenceItem
import me.cael.ancientthaumaturgy.utils.identifier
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.texture.Sprite
import net.minecraft.client.util.SpriteIdentifier
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.screen.PlayerScreenHandler
import net.minecraft.state.property.Properties
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3f

class SealRenderer : BlockEntityRenderer<SealBlockEntity> {
    override fun render(entity: SealBlockEntity, tickDelta: Float, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int) {
        if (entity.cachedState.get(Properties.ENABLED) as Boolean) entity.lastRenderDegree += MathHelper.lerp(tickDelta, 0f, .3f)
        if (entity.lastRenderDegree >= 360) entity.lastRenderDegree = 0f
        if (entity.runes.length > 0) renderRune("inner/" + EssenceItem.Type.fromId(entity.runes[0].toString()).name.lowercase(), 0.996f, -1f, entity, matrices, vertexConsumers, overlay)
        if (entity.runes.length > 1) renderRune("outer/" + EssenceItem.Type.fromId(entity.runes[1].toString()).name.lowercase(), 0.95f, 1f, entity, matrices, vertexConsumers, overlay)
        if (entity.runes.length > 2) renderRune("center/" + EssenceItem.Type.fromId(entity.runes[2].toString()).name.lowercase(), 0.9f, 0f, entity, matrices, vertexConsumers, overlay)
    }

    private fun renderRune(rune: String, offset: Float, rotationOffset: Float, entity: SealBlockEntity, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, overlay: Int) {
        val sealIdentifier = SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, identifier("rune/$rune"))
        val sealConsumer = sealIdentifier.getVertexConsumer(vertexConsumers) { texture -> RenderLayer.getEntityTranslucent(texture) }
        val direction = entity.getDirection()
        val directionVector = direction.opposite.unitVector
        val rotationVector = when(direction) {
            Direction.UP -> Vec3f.NEGATIVE_Y
            Direction.DOWN -> Vec3f.POSITIVE_Y
            Direction.NORTH -> Vec3f.POSITIVE_Z
            Direction.SOUTH -> Vec3f.NEGATIVE_Z
            Direction.EAST -> Vec3f.NEGATIVE_X
            Direction.WEST -> Vec3f.POSITIVE_X
            else -> Vec3f.POSITIVE_Z
        }
        directionVector.multiplyComponentwise(offset, offset, offset)
        matrices.push()
        matrices.translate(0.5,0.5,0.5)
        matrices.multiply(rotationVector.getDegreesQuaternion(entity.lastRenderDegree * rotationOffset))
        matrices.translate(-0.5,-0.5,-0.5)
        matrices.translate(directionVector.x * 1.1093, directionVector.y * 1.1093, directionVector.z * 1.1093)
        when(direction) {
            Direction.UP -> renderVertices(sealConsumer, sealIdentifier.sprite, matrices.peek(), Direction.UP.unitVector, overlay, 15728880, -0.1094f, 1.1094f, 1.1094f, 1.1094f, 1.1094f, 1.1094f, -0.1094f, -0.1094f) //Direction.UP
            Direction.DOWN -> renderVertices(sealConsumer, sealIdentifier.sprite, matrices.peek(), Direction.DOWN.unitVector, overlay, 15728880, -0.1094f, 1.1094f, -0.1094f, -0.1094f, -0.1094f, -0.1094f, 1.1094f, 1.1094f) //Direction.DOWN
            Direction.SOUTH -> renderVertices(sealConsumer, sealIdentifier.sprite, matrices.peek(), Direction.SOUTH.unitVector, overlay, 15728880, -0.1094f, 1.1094f, -0.1094f, 1.1094f, 1.1094f, 1.1094f, 1.1094f, 1.1094f) //Direction.SOUTH
            Direction.NORTH -> renderVertices(sealConsumer, sealIdentifier.sprite, matrices.peek(), Direction.NORTH.unitVector, overlay, 15728880, -0.1094f, 1.1094f, 1.1094f, -0.1094f, -0.1094f, -0.1094f, -0.1094f, -0.1094f) //Direction.NORTH
            Direction.EAST -> renderVertices(sealConsumer, sealIdentifier.sprite, matrices.peek(), Direction.EAST.unitVector, overlay, 15728880, 1.1094f, 1.1094f, 1.1094f, -0.1094f, -0.1094f, 1.1094f, 1.1094f, -0.1094f) //Direction.EAST
            Direction.WEST -> renderVertices(sealConsumer, sealIdentifier.sprite, matrices.peek(), Direction.WEST.unitVector, overlay, 15728880, -0.1094f, -0.1094f, -0.1094f, 1.1094f, -0.1094f, 1.1094f, 1.1094f, -0.1094f) //Direction.WEST
            else -> return
        }
        matrices.pop()
    }

    private fun renderVertices(bb: VertexConsumer, sprite: Sprite, entry: MatrixStack.Entry, normal: Vec3f, overlay: Int, light: Int, f: Float, g: Float, h: Float, i: Float, j: Float, k: Float, l: Float, m: Float) {
        bb.vertex(entry.positionMatrix, f, h, j).color(1f, 1f, 1f, 1f).texture(sprite.maxU, sprite.minV).overlay(overlay).light(light).normal(entry.normalMatrix, normal.x, normal.y, normal.z).next()
        bb.vertex(entry.positionMatrix, g, h, k).color(1f, 1f, 1f, 1f).texture(sprite.minU, sprite.minV).overlay(overlay).light(light).normal(entry.normalMatrix, normal.x, normal.y, normal.z).next()
        bb.vertex(entry.positionMatrix, g, i, l).color(1f, 1f, 1f, 1f).texture(sprite.minU, sprite.maxV).overlay(overlay).light(light).normal(entry.normalMatrix, normal.x, normal.y, normal.z).next()
        bb.vertex(entry.positionMatrix, f, i, m).color(1f, 1f, 1f, 1f).texture(sprite.maxU, sprite.maxV).overlay(overlay).light(light).normal(entry.normalMatrix, normal.x, normal.y, normal.z).next()
    }
}