package me.cael.ancientthaumaturgy.utils

import me.cael.ancientthaumaturgy.AncientThaumaturgy
import me.cael.ancientthaumaturgy.blocks.BlockRegistry.getContainerInfo
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.minecraft.block.Block
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box

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

class BlockScreenHandlerFactory(val block: Block, val pos: BlockPos): ExtendedScreenHandlerFactory {
    override fun createMenu(syncId: Int, playerInv: PlayerInventory, player: PlayerEntity): ScreenHandler {
        val world = player.world
        val be = world.getBlockEntity(pos)
//        return getContainerInfo(block)!!.handlerClass.java.constructors[0].newInstance(
//                syncId, playerInv, be, ScreenHandlerContext.create(world, pos)
//        ) as ScreenHandler
        return getContainerInfo(block)!!.handlerClass.java.constructors[0].newInstance(
                syncId, playerInv, be
        ) as ScreenHandler
    }

    override fun writeScreenOpeningData(p0: ServerPlayerEntity?, p1: PacketByteBuf?) {
        p1?.writeBlockPos(pos)
    }

    override fun getDisplayName() = getContainerInfo(block)!!.title
}