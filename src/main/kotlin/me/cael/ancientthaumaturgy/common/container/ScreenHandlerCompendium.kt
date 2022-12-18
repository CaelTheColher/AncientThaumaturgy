package me.cael.ancientthaumaturgy.common.container

import me.cael.ancientthaumaturgy.client.screen.InfuserScreen
import me.cael.ancientthaumaturgy.common.blockentity.InfuserBlockEntity
import me.cael.ancientthaumaturgy.utils.RegistryCompendium
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType
import net.minecraft.registry.Registries
import net.minecraft.screen.ScreenHandlerType

object ScreenHandlerCompendium: RegistryCompendium<ScreenHandlerType<*>>(Registries.SCREEN_HANDLER) {
    val INFUSER_BLOCK = register("infuser_block", ExtendedScreenHandlerType { i, playerInventory, packetByteBuf ->
        val pos = packetByteBuf.readBlockPos()
        val player = playerInventory.player
        val world = player.world
        val be = world.getBlockEntity(pos) as InfuserBlockEntity
        InfuserScreenHandler(i, playerInventory, be)
    }) as ScreenHandlerType<InfuserScreenHandler>

    fun onInitializeClient() {
        ScreenRegistry.register(INFUSER_BLOCK) {handler, playerInventory, title -> InfuserScreen(handler, playerInventory, title)}
    }

}