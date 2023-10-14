package me.cael.ancientthaumaturgy.common.entity

import net.minecraft.client.MinecraftClient
import net.minecraft.client.particle.Particle
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.PersistentProjectileEntity
import net.minecraft.item.ItemStack
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.world.World

class FireParticleEntity : PersistentProjectileEntity {

    private val maxTicks = 30
    private var ticks = 0
    var particle: Particle? = null

    constructor(entityType: EntityType<FireParticleEntity>, world: World): super(entityType, world)

    constructor(world: World, owner: LivingEntity) : super(EntityCompendium.FIRE_PARTICLE, owner, world) {
        this.setNoGravity(true)
        this.damage = 5.0
    }

    override fun tick() {
        super.tick()
        val toDiscard = ticks++ >= maxTicks
        if (world.isClient) {
            if (particle == null) particle = MinecraftClient.getInstance().particleManager.addParticle(ParticleTypes.FLAME, x, y, z, 0.0, 0.0, 0.0)
            val x = this.x + velocity.x
            val y = this.y + velocity.y
            val z = this.z + velocity.z
            particle?.maxAge = maxTicks
            particle?.setPos(x, y, z)
            if (toDiscard) {
                particle?.markDead()
            }
        }
        if(toDiscard) {
            this.discard()
        }
    }

    override fun onEntityHit(entityHitResult: EntityHitResult) {
        val entity = entityHitResult.entity
        if (entity is LivingEntity) {
            entity.setOnFireFor(1)
            entity.damage(entity.damageSources.mobProjectile(this, owner as LivingEntity), this.damage.toFloat())
        }
    }

    override fun onBlockHit(blockHitResult: BlockHitResult?) {
        this.discard()
    }

    override fun isCritical(): Boolean = false
    override fun isShotFromCrossbow(): Boolean = false
    override fun hasNoGravity(): Boolean = true
    override fun asItemStack(): ItemStack? = null
}