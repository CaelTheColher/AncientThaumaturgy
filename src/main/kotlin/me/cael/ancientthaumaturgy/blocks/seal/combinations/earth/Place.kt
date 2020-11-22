package me.cael.ancientthaumaturgy.blocks.seal.combinations.earth

import me.cael.ancientthaumaturgy.blocks.seal.SealBlockEntity
import me.cael.ancientthaumaturgy.blocks.seal.combinations.AbstractSealCombination
import me.cael.ancientthaumaturgy.utils.forEach
import me.cael.ancientthaumaturgy.utils.getStack
import net.minecraft.inventory.Inventory
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundCategory
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.world.World

class Place(range: Double, depth: Double) : AbstractSealCombination(10, range, depth) {
    override fun tick(seal: SealBlockEntity) {
        val area = getArea(seal.pos, seal.getDirection())
        area.forEach{ x,y,z ->
            val pos = BlockPos(x,y,z)
            val world = seal.world!!
            val blockStack: ItemStack = getLinkedInventory(seal)?.getStack { stack -> stack.item is BlockItem } ?: return
            if (tryPlace(world, pos, blockStack)) return
        }
    }

    private fun tryPlace(world: World, pos: BlockPos, stack: ItemStack) : Boolean {
        val state = world.getBlockState(pos)
        val block = (stack.item as BlockItem).block
        val canPlace = (state.material.isReplaceable  && block.defaultState.canPlaceAt(world, pos))
        if (canPlace && world.setBlockState(pos, block.defaultState)) {
            val soundGroup = block.defaultState.soundGroup
            world.playSound(null, pos, soundGroup.placeSound, SoundCategory.BLOCKS, soundGroup.volume, soundGroup.pitch)
            stack.decrement(1)
            return true
        }
        return false
    }
}