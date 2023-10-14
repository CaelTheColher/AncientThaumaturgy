package me.cael.ancientthaumaturgy.common.blockentity
import me.cael.ancientthaumaturgy.vis.api.VisStorage
import me.cael.ancientthaumaturgy.vis.api.VisStorageUtil
import me.cael.ancientthaumaturgy.vis.api.base.SimpleVisStorage
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

open class MachineEntity(type: BlockEntityType<*>, pos: BlockPos, state: BlockState, storageMax: Long = 0, inputMax: Long = 0, outputMax: Long = 0) : BlockEntity(type, pos, state) {

    open val visStorage = object : SimpleVisStorage(storageMax, inputMax, outputMax) {
        override fun onFinalCommit() {
            markDirtyAndSync()
        }
    }

    fun getPressure() = visStorage.amount * visStorage.capacity

    open fun tick() {
        if (visStorage.supportsExtraction()) {
            val targets = linkedSetOf<VisStorage>()
            Direction.entries.forEach { direction ->
                val targetPos = pos.offset(direction)
                if (!(world!!.getBlockEntity(targetPos) is TankBlockEntity && this is TankBlockEntity)) {
                    VisStorage.SIDED.find(world, targetPos, direction.opposite)?.let { target ->
                        if (target.supportsInsertion() && target.amount < target.capacity) {
                            targets.add(target)
                        }
                    }
                }
            }
            if (targets.size > 0) {
                val transferAmount = visStorage.amount.coerceAtMost(visStorage.maxExtract) / targets.size
                targets.forEach { target ->
                    VisStorageUtil.move(visStorage, target, transferAmount, null)
                }
            }
        }
    }

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

    companion object {
        @Suppress("unused_parameter")
        fun <G: MachineEntity> ticker(world: World, pos: BlockPos, state: BlockState, entity: G) {
            if (!world.isClient) {
                entity.tick()
            }
        }
    }

}