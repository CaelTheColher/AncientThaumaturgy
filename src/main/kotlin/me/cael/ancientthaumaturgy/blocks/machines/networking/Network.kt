package me.cael.ancientthaumaturgy.blocks.machines.networking

import me.cael.ancientthaumaturgy.blocks.machines.MachineEntity
import me.cael.ancientthaumaturgy.blocks.machines.tube.TubeBlock
import me.cael.ancientthaumaturgy.blocks.machines.tube.TubeBlockEntity
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.LongTag
import net.minecraft.nbt.StringTag
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.chunk.Chunk

class Network(
        val world: ServerWorld,
        val tubes: MutableSet<BlockPos> = hashSetOf(),
        val machines: MutableMap<BlockPos, MutableSet<Direction>> = hashMapOf()
) {

    fun remove() {
        val state = NetworkState.getNetworkState(world)
        state.networks.remove(this)
        tubes.forEach { state.networksByPos.remove(it) }
    }

    fun appendTube(state: NetworkState, blockPos: BlockPos) {
        tubes.add(blockPos)
        state.networksByPos[blockPos] = this
    }

    fun appendMachine(blockPos: BlockPos, direction: Direction) {
        machines.computeIfAbsent(blockPos) { hashSetOf() }.add(direction)
    }

    fun toTag(tag: CompoundTag) {
        val tubesList = ListTag()
        tubes.forEach { pos ->
            tubesList.add(LongTag.of(pos.asLong()))
        }
        val machinesList = ListTag()
        machines.forEach { (pos, directions) ->
            val machineTag = CompoundTag()
            machineTag.putLong("pos", pos.asLong())
            val dirList = ListTag()
            directions.forEach { dir ->
                dirList.add(StringTag.of(dir.toString()))
            }
            machineTag.put("dir", dirList)
            machinesList.add(machineTag)
        }
        tag.put("tubes", tubesList)
        tag.put("machines", machinesList)
    }

    companion object {
        fun handleBreak(world: ServerWorld, pos: BlockPos) {
            val state = NetworkState.getNetworkState(world)
            if (state.networksByPos.containsKey(pos))
                state.networksByPos[pos]?.remove()
            Direction.values().forEach {
                val offset = pos.offset(it)
                handleUpdate(world, offset)
            }
        }

        fun handleUpdate(world: ServerWorld, pos: BlockPos) {
            val state = NetworkState.getNetworkState(world)
            if (state.networksByPos.containsKey(pos))
                state.networksByPos[pos]?.remove()
            val network = Network(world)
            state.networks.add(network)
            val scanned = hashSetOf<BlockPos>()
            Direction.values().forEach { dir ->
                buildNetwork(scanned, state, network, world.getChunk(pos), world, pos, pos, dir)
            }
            if (network.machines.isEmpty() || network.tubes.isEmpty())
                network.remove()
            state.markDirty()
        }

        private fun buildNetwork(scanned: MutableSet<BlockPos>, state: NetworkState, network: Network, chunk: Chunk, world: ServerWorld, blockPos: BlockPos, source: BlockPos, direction: Direction) {
            if (network.machines.containsKey(blockPos)) {
                network.appendMachine(blockPos, direction.opposite)
                return
            }
            if (blockPos != source && !scanned.add(blockPos)) return
            val blockEntity = chunk.getBlockEntity(blockPos) ?: return
            if (blockEntity is TubeBlockEntity) {
                if (state.networksByPos.containsKey(blockPos)) {
                    val oldNetwork = state.networksByPos[blockPos]
                    if (state.networks.contains(oldNetwork) && oldNetwork != network) {
                        oldNetwork?.remove()
                    }
                }
                val blockState = chunk.getBlockState(blockPos)
                Direction.values().forEach { dir ->
                    if (blockState[TubeBlock.getProperty(dir)]) {
                        val nPos = blockPos.offset(dir)
                        if (nPos.x shr 4 == chunk.pos.x && nPos.z shr 4 == chunk.pos.z)
                            buildNetwork(scanned, state, network, chunk, world, nPos, source, dir)
                        else
                            buildNetwork(scanned, state, network, world.getChunk(nPos), world, nPos, source, dir)
                    }
                }
                if (blockState[TubeBlock.getProperty(direction)])
                    network.appendTube(state, blockPos.toImmutable())
            } else if (blockEntity is MachineEntity) {
                network.appendMachine(blockPos, direction.opposite)
            }
        }

        fun fromTag(world: ServerWorld, tag: CompoundTag): Network {
            val tubesList = tag.getList("tubes", 4)
            val machinesList = tag.getList("machines", 10)
            val network = Network(world)
            tubesList.forEach { tubeTag ->
                tubeTag as LongTag
                network.tubes.add(BlockPos.fromLong(tubeTag.long).toImmutable())
            }
            machinesList.forEach { machineTag ->
                machineTag as CompoundTag
                val posLong = machineTag.getLong("pos")
                val pos = BlockPos.fromLong(posLong)
                val dirList = machineTag.getList("dir", 8)
                val directions = hashSetOf<Direction>()
                dirList.forEach { dirTag ->
                    dirTag as StringTag
                    val dir = Direction.valueOf(dirTag.asString().toUpperCase())
                    directions.add(dir)
                }
                network.machines[pos] = directions
            }
            return network
        }
    }
}