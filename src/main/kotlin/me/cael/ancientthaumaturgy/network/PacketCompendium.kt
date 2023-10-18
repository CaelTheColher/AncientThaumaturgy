package me.cael.ancientthaumaturgy.network

import me.cael.ancientthaumaturgy.client.screen.InfuserScreen
import me.cael.ancientthaumaturgy.mixed.MixedBlockEntity
import me.cael.ancientthaumaturgy.utils.identifier
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking

object PacketCompendium {
    val UPDATE_INFUSER_SCREEN = identifier("update_infuser_screen")
    val UPDATE_BLOCK_ENTITY = identifier("update_block_entity")

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
        ClientPlayNetworking.registerGlobalReceiver(UPDATE_BLOCK_ENTITY) { client, _, buf, _ ->
            val pos = buf.readBlockPos()
            val timer = buf.readInt()
            client.execute {
                val blockEntity = client.world?.getBlockEntity(pos)
                val state = client.world?.getBlockState(pos)
                (blockEntity as? MixedBlockEntity)?.ancientThaumaturgy_setPortableHoleTimer(timer)
                client.worldRenderer.updateBlock(client.world, pos, state, state, 0)
            }
        }
    }

}