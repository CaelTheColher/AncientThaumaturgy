package me.cael.ancientthaumaturgy.blocks.machines.infuser

import me.cael.ancientthaumaturgy.blocks.BlockRegistry.INFUSER_BLOCK
import me.cael.ancientthaumaturgy.blocks.BlockRegistry.getContainerInfo
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot

class InfuserScreenHandler(i: Int, playerInventory: PlayerInventory, val inventory: InfuserBlockEntity) : ScreenHandler(getContainerInfo(INFUSER_BLOCK)?.handlerType, i) {
    init {
        checkSize(inventory, 8)
        inventory.onOpen(playerInventory.player)

        addSlot(Slot(inventory, 0, 200, 100))

        (0..2).forEach {n ->
            (0..8).forEach {m ->
                addSlot(Slot(playerInventory, m + n * 9 + 9, 8 + m * 18, 121 + n*18))
            }
        }

        (0..8).forEach { n ->
            addSlot(Slot(playerInventory, n, 8 + n * 18, 179))
        }
    }

    override fun canUse(player: PlayerEntity): Boolean = inventory.canPlayerUse(player)

    override fun transferSlot(player: PlayerEntity?, invSlot: Int): ItemStack? {
        var itemStack = ItemStack.EMPTY
        val slot = this.slots[invSlot]
        if (slot != null && slot.hasStack()) {
            val itemStack2 = slot.stack
            itemStack = itemStack2.copy()
            if (invSlot < 27) {
                if (!insertItem(itemStack2, 27, this.slots.size, true)) {
                    return ItemStack.EMPTY
                }
            } else if (!insertItem(itemStack2, 0, 27, false)) {
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
}