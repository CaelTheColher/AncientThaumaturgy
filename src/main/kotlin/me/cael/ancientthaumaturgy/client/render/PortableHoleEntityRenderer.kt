package me.cael.ancientthaumaturgy.client.render

import me.cael.ancientthaumaturgy.common.item.PortableHoleItem
import me.cael.ancientthaumaturgy.mixed.MixedBlockEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import org.joml.Matrix4f

class PortableHoleEntityRenderer<T: BlockEntity>  : BlockEntityRenderer<T>{

    private val random = Random.create()

    override fun render(entity: T, tickDelta: Float, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int) {
        matrices.push()
        val m = matrices.peek().positionMatrix
        val world = entity.world ?: return
        val vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEndPortal())
        val g = 0.15f
        val red = (random.nextFloat() * 0.5f + 0.1f) * g
        val green = (random.nextFloat() * 0.5f + 0.4f) * g
        val blue = (random.nextFloat() * 0.5f + 0.5f) * g
        for (direction in Direction.entries) {
            val pos = entity.pos.offset(direction)
            val newEntity = world.getBlockEntity(pos)
            val isPortableHole = newEntity != null && (newEntity as MixedBlockEntity).ancientThaumaturgy_getPortableHoleTimer() > 0
            if(!PortableHoleItem.canGoThrough(pos, world) && !isPortableHole) {
                when(direction) {
                    Direction.SOUTH -> renderVertices(m, vertexConsumer, 0.0f, 1.0f, 1.0f, 0.0f, 0.999f, 0.999f, 0.999f, 0.999f, red, green, blue)
                    Direction.NORTH -> renderVertices(m, vertexConsumer, 0.0f, 1.0f, 0.0f, 1.0f, 0.001f, 0.001f, 0.001f, 0.001f, red, green, blue)
                    Direction.EAST  -> renderVertices(m, vertexConsumer, 0.999f, 0.999f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, red, green, blue)
                    Direction.WEST  -> renderVertices(m, vertexConsumer, 0.001f, 0.001f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, red, green, blue)
                    Direction.DOWN  -> renderVertices(m, vertexConsumer, 0.0f, 1.0f, 0.001f, 0.001f, 1.0f, 1.0f, 0.0f, 0.0f, red, green, blue)
                    Direction.UP    -> renderVertices(m, vertexConsumer, 0.0f, 1.0f, 0.999f, 0.999f, 0.0f, 0.0f, 1.0f, 1.0f, red, green, blue)
                }
            }
        }
        matrices.pop()
    }

    private fun renderVertices(matrix4f: Matrix4f, vertexConsumer: VertexConsumer, f: Float, g: Float, h: Float, i: Float, j: Float, k: Float, l: Float, m: Float, red: Float, green: Float, blue: Float) {
        vertexConsumer.vertex(matrix4f, f, h, j).color(red, green, blue, 1.0f).next()
        vertexConsumer.vertex(matrix4f, g, h, k).color(red, green, blue, 1.0f).next()
        vertexConsumer.vertex(matrix4f, g, i, l).color(red, green, blue, 1.0f).next()
        vertexConsumer.vertex(matrix4f, f, i, m).color(red, green, blue, 1.0f).next()
    }

}