package me.cael.ancientthaumaturgy.blocks.machines.infuser

import me.cael.ancientthaumaturgy.blocks.BlockRegistry.INFUSER_BLOCK
import me.cael.ancientthaumaturgy.blocks.BlockRegistry.getContainerInfo
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot

class InfuserScreenHandler(i: Int, playerInventory: PlayerInventory, val inventory: InfuserBlockEntity) : ScreenHandler(getContainerInfo(INFUSER_BLOCK)?.handlerType, i) {
    companion object {
        class InfuserResultSlot(inventory: Inventory, index: Int, x: Int, y: Int) : Slot(inventory, index, x, y) {
            override fun canInsert(stack: ItemStack): Boolean = false
        }
    }

    init {
        checkSize(inventory, 8)
        inventory.onOpen(playerInventory.player)

        addSlot(Slot(inventory, 0, 80, 18))
        addSlot(Slot(inventory, 1, 49, 51))
        addSlot(Slot(inventory, 2, 111, 51))
        addSlot(Slot(inventory, 3, 35, 91))
        addSlot(Slot(inventory, 4, 125, 91))
        addSlot(Slot(inventory, 5, 80, 103))
        addSlot(InfuserResultSlot(inventory, 6, 80, 68))
        addSlot(InfuserResultSlot(inventory, 7, 80, 124))

        (0..2).forEach {n ->
            (0..8).forEach {m ->
                addSlot(Slot(playerInventory, m + n * 9 + 9, 8 + m * 18, 121+37 + n*18))
            }
        }

        (0..8).forEach { n ->
            addSlot(Slot(playerInventory, n, 8 + n * 18, 179+37))
        }
    }
    
    override fun transferSlot(player: PlayerEntity?, invSlot: Int): ItemStack? {
        var itemStack = ItemStack.EMPTY
        val slot = this.slots[invSlot]
        if (slot != null && slot.hasStack()) {
            val itemStack2 = slot.stack
            itemStack = itemStack2.copy()
            if (invSlot < 8) {
                if (!insertItem(itemStack2, 8, this.slots.size, true)) {
                    return ItemStack.EMPTY
                }
            } else if (!insertItem(itemStack2, 0, 8, false)) {
                return ItemStack.EMPTY
            }
            if (itemStack2.isEmpty) {
                slot.stack = ItemStack.EMPTY
            } else {
                slot.markDirty()
            }
        }
        return itemStack
    }

    override fun canUse(player: PlayerEntity): Boolean = inventory.canPlayerUse(player)
}