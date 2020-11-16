package me.cael.ancientthaumaturgy.utils

import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.util.math.Box

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