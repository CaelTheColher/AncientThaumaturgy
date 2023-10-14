package me.cael.ancientthaumaturgy.common.blockentity

import me.cael.ancientthaumaturgy.common.recipe.CrucibleRecipe
import net.minecraft.block.BlockState
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box

class CrucibleBlockEntity(pos: BlockPos, state: BlockState) : MachineEntity(BlockEntityCompendium.CRUCIBLE_BLOCK_TYPE, pos, state, 1000, 0, 100), Inventory {

    var inventory: DefaultedList<ItemStack> = DefaultedList.ofSize(1, ItemStack.EMPTY)
    var lastRenderedAmount = 0f
    var visPerTick = 0L
    var smeltTime = 0

    override fun tick() {
        super.tick()
        if (smeltTime > 0 ) {
            val toAdd = visPerTick.coerceAtMost((visStorage.capacity - visStorage.amount))
            visStorage.amount += toAdd
            --smeltTime
            return
        } else {
            visPerTick = 0
            val match = world!!.recipeManager.getFirstMatch(CrucibleRecipe.TYPE, this, world)
            if (inventory[0].count > 0 && visStorage.amount < visStorage.capacity && match.isPresent) {
                --inventory[0].count
                val recipe = match.get()
                visPerTick = recipe.visPerTick
                smeltTime += recipe.smeltTime
            }
        }
        checkForItems()
        markDirtyAndSync()
    }

    private fun checkForItems() {
        val itemEntities = world!!.getOtherEntities(null, Box(pos)) { it is ItemEntity && (inventory[0].isEmpty || inventory[0].item == (it.stack.item) ) }
        if (itemEntities.isEmpty()) return
        val itemEntity = itemEntities.removeFirst() as ItemEntity
        val item = itemEntity.stack
        if (inventory[0].isEmpty) {
            inventory[0] = item.copy()
            item.count = 0
        }
        if (item.item == inventory[0].item) {
            val remaining = inventory[0].maxCount - inventory[0].count
            val toAdd = if(item.count >= remaining) remaining else item.count
            inventory[0].count += toAdd
            item.count -= toAdd
        }
    }

    override fun writeNbt(nbt: NbtCompound){
        Inventories.writeNbt(nbt, this.inventory)
        super.writeNbt(nbt)
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        this.inventory = DefaultedList.ofSize(size(), ItemStack.EMPTY)
        Inventories.readNbt(nbt, this.inventory)
    }

    override fun clear() = inventory.clear()

    override fun size(): Int = inventory.size

    override fun isEmpty(): Boolean = inventory.all{ it.isEmpty }

    override fun getStack(slot: Int): ItemStack = inventory[slot]

    override fun removeStack(slot: Int, amount: Int): ItemStack = Inventories.splitStack(inventory, slot, amount)

    override fun removeStack(slot: Int): ItemStack = Inventories.removeStack(inventory, slot)

    override fun setStack(slot: Int, stack: ItemStack?) {
        inventory[slot] = stack
        if (stack!!.count > maxCountPerStack) {
            stack.count = maxCountPerStack
        }
    }

    override fun canPlayerUse(player: PlayerEntity?): Boolean = false

}