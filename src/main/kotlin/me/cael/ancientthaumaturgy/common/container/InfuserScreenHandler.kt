package me.cael.ancientthaumaturgy.common.container

import me.cael.ancientthaumaturgy.common.blockentity.InfuserBlockEntity
import me.cael.ancientthaumaturgy.network.PacketCompendium
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot
import net.minecraft.server.network.ServerPlayerEntity

class InfuserScreenHandler(i: Int, val playerInventory: PlayerInventory, val inventory: InfuserBlockEntity) : ScreenHandler(ScreenHandlerCompendium.INFUSER_BLOCK, i) {

    var ticks = 0
    var infuseTime = 0

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
    
    override fun quickMove(player: PlayerEntity?, invSlot: Int): ItemStack? {
        var itemStack = ItemStack.EMPTY
        val slot = this.slots[invSlot]
        if (slot.hasStack()) {
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

    fun getInfusionProgress(): Int {
        if (ticks == 0 || infuseTime == 0) {
            return 0
        }
        return ticks * 39 / infuseTime
    }

    fun shouldSync() : Boolean {
        return (inventory.ticks != ticks) || (inventory.infuseTime != infuseTime)
    }

    fun postSync() {
        ticks = inventory.ticks
        infuseTime = inventory.infuseTime
    }

    fun writeToBuf(buf: PacketByteBuf) {
        buf.writeInt(inventory.ticks)
        buf.writeInt(inventory.infuseTime)
    }

    override fun sendContentUpdates() {
        (playerInventory.player as? ServerPlayerEntity)?.let { player ->
            if(shouldSync()) {
                ServerPlayNetworking.send(player, PacketCompendium.UPDATE_INFUSER_SCREEN, PacketByteBufs.create().also { writeToBuf(it) })
                postSync()
            }
        }
        super.sendContentUpdates()
    }

    override fun canUse(player: PlayerEntity): Boolean = inventory.canPlayerUse(player)

}