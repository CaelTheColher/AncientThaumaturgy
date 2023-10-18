package me.cael.ancientthaumaturgy.common.item.staff

import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.ProjectileUtil
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class LightningWandItem : Item(Settings().maxCount(1)) {

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        if (world.isClient) return TypedActionResult.pass(user.getStackInHand(hand))
        val rotation: Vec3d = user.rotationVector
        val range = 150.0 // No idea what this number actually does
        val hitResult = ProjectileUtil.raycast(user,
            user.getCameraPosVec(1.0f),
            user.getCameraPosVec(1.0f).add(rotation.multiply(range)),
            user.boundingBox.stretch(rotation.multiply(range)).expand(1.0, 1.0, 1.0),
            { entity: Entity -> !entity.isSpectator && entity.canHit()},
            range
        )
        if (hitResult != null) {
            val hitEntity = hitResult.entity
            hitEntity.damage(world.damageSources.playerAttack(user), 5f)
            user.itemCooldownManager.set(this, 10)
            return TypedActionResult.success(user.getStackInHand(hand))
        }
        return TypedActionResult.fail(user.getStackInHand(hand))
    }

    override fun getMaxUseTime(stack: ItemStack?): Int = 72000
}