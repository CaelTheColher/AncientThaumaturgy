package me.cael.ancientthaumaturgy.client.render

import me.cael.ancientthaumaturgy.common.entity.FireParticleEntity
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

class FireParticleEntityRenderer(ctx: EntityRendererFactory.Context) : EntityRenderer<FireParticleEntity>(ctx) {
    override fun render(entity: FireParticleEntity, yaw: Float, tickDelta: Float, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int) {}
    override fun getTexture(entity: FireParticleEntity?): Identifier? = null
}