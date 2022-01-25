package me.cael.ancientthaumaturgy.network

import me.cael.ancientthaumaturgy.client.screen.InfuserScreen
import me.cael.ancientthaumaturgy.utils.identifier
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking

object PacketCompendium {
    val UPDATE_INFUSER_SCREEN = identifier("update_infuser_screen")

    fun onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(UPDATE_INFUSER_SCREEN) { client, _, buf, _ ->
            val int1 = buf.readInt()
            val int2 = buf.readInt()
            client.execute {
                (client.currentScreen as? InfuserScreen)?.screenHandler?.let {
                    it.ticks = int1
                    it.infuseTime = int2
                }
            }
        }
    }

}