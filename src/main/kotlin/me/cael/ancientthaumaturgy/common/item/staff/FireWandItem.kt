package me.cael.ancientthaumaturgy.common.item.staff

import me.cael.ancientthaumaturgy.common.entity.FireParticleEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.UseAction
import net.minecraft.world.World

class FireWandItem : Item(Settings().maxCount(1)) {

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        user.setCurrentHand(hand)
        return TypedActionResult.consume(user.getStackInHand(hand))
    }

    override fun usageTick(world: World, user: LivingEntity, stack: ItemStack, remainingUseTicks: Int) {
        if (world.isClient) return
        repeat(3) {
            val fireEntity = FireParticleEntity(world, user)
            val rot = user.rotationVector
            fireEntity.setVelocity(rot.x, rot.y, rot.z, 0.3f, 4.0f)
            world.spawnEntity(fireEntity)
        }
        return
    }

    override fun getUseAction(stack: ItemStack?): UseAction = UseAction.BOW
    override fun getMaxUseTime(stack: ItemStack?): Int = 72000
}