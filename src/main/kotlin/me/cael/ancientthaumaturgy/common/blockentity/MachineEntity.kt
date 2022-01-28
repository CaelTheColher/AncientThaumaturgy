package me.cael.ancientthaumaturgy.common.blockentity
import me.cael.ancientthaumaturgy.vis.api.base.SimpleEnergyStorage
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.Packet
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos

open class MachineEntity(type: BlockEntityType<*>, pos: BlockPos, state: BlockState, storageMax: Long = 0, inputMax: Long = 0, outputMax: Long = 0) : BlockEntity(type, pos, state) {

    open val visStorage = object : SimpleEnergyStorage(storageMax, inputMax, outputMax) {
        override fun onFinalCommit() {
            markDirtyAndSync()
        }
    }

    fun getPressure() = visStorage.amount * visStorage.capacity

    override fun toUpdatePacket(): Packet<ClientPlayPacketListener>? {
        return BlockEntityUpdateS2CPacket.create(this) { NbtCompound().also(::writeNbt) }
    }

    override fun toInitialChunkDataNbt(): NbtCompound {
        return NbtCompound().also(::writeNbt)
    }

    fun sync() {
        ((this as? BlockEntity)?.world as? ServerWorld)?.chunkManager?.markForUpdate(this.pos)
    }

    fun markDirtyAndSync() = markDirty().also{ sync() }

    override fun writeNbt(nbt: NbtCompound) {
        nbt.putLong("storedVis", visStorage.amount)
        super.writeNbt(nbt)
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        visStorage.amount = nbt.getLong("storedVis")
    }

}