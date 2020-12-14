package me.cael.ancientthaumaturgy.blocks.machines.networking

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.world.PersistentState
import java.util.*

class NetworkState(private val world: ServerWorld) : PersistentState("ancientthaumaturgy_networks") {
    var networks = hashSetOf<Network>()
    val networksByPos = hashMapOf<BlockPos, Network>()

    override fun toTag(tag: CompoundTag): CompoundTag {
        val list = ListTag()
        networks.map {
            val networkTag = CompoundTag()
            it.toTag(networkTag)
            networkTag
        }.forEach { list.add(it) }
        tag.put("networks", list)
        return tag
    }

    override fun fromTag(tag: CompoundTag) {
        val list = tag.getList("networks", 10)
        networks = list.map { networkTag ->
            Network.fromTag(world, networkTag as CompoundTag)
        }.toHashSet()
        networks.forEach { network ->
            network.tubes.forEach { pos -> networksByPos[pos] = network }
        }
    }

    companion object {
        val NETWORK_STATES = WeakHashMap<ServerWorld, NetworkState>()
        fun getNetworkState(world: ServerWorld) = world.persistentStateManager.getOrCreate({ NetworkState(world) }, "ancientthaumaturgy_networks")
    }
}