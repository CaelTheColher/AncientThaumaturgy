package me.cael.ancientthaumaturgy.utils

import me.cael.ancientthaumaturgy.AncientThaumaturgy
import net.minecraft.block.entity.BlockEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.world.World

fun identifier(id: String) = Identifier(AncientThaumaturgy.NAMESPACE, id)

inline fun Box.forEach(f: (Int, Int, Int) -> Unit) {
    for (y in minY.toInt()..maxY.toInt())
        for (x in minX.toInt()..maxX.toInt())
            for (z in minZ.toInt()..maxZ.toInt())
                f(x, y, z)
}

inline fun Inventory.forEach(f: (ItemStack) -> Unit) {
    for(i in 0 until this.size())
        f(this.getStack(i))
}

inline fun Inventory.getStack(f: (ItemStack) -> Boolean) : ItemStack? {
    this.forEach {stack ->
        if (f(stack)) return stack
    }
    return null
}

fun BlockPos.toNbt() : NbtCompound {
    val nbt = NbtCompound()
    nbt.putInt("x", x)
    nbt.putInt("y", y)
    nbt.putInt("z", z)
    return nbt
}

fun Direction.toDegrees() : Float = when (this) {
    Direction.EAST , Direction.WEST -> this.opposite.asRotation()
    else -> this.asRotation()
}

data class PortalDestination(val pos: BlockPos, val world: RegistryKey<World>) {
    fun toNbt() : NbtCompound {
        val nbt = NbtCompound()
        nbt.put("pos", pos.toNbt())
        nbt.putString("world", world.value.toString())
        return nbt
    }
    companion object {
        fun fromNbt(nbt: NbtCompound) : PortalDestination {
            val pos = BlockEntity.posFromNbt(nbt.getCompound("pos"))
            val world = RegistryKey.of(RegistryKeys.WORLD, Identifier(nbt.getString("world")))
            return PortalDestination(pos, world)
        }
    }
}