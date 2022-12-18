package me.cael.ancientthaumaturgy.common.blockentity

import me.cael.ancientthaumaturgy.common.blockentity.sealcombination.CombinationRegistry
import me.cael.ancientthaumaturgy.utils.toNbt
import net.minecraft.block.BlockState
import net.minecraft.block.WallMountedBlock
import net.minecraft.block.enums.WallMountLocation
import net.minecraft.nbt.NbtCompound
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import java.util.*

class SealBlockEntity(pos: BlockPos, state: BlockState) : MachineEntity(BlockEntityCompendium.SEAL_BLOCK_TYPE, pos, state) {
    var lastRenderDegree = 0f
    var runes = ""
    var counter = 0
    var linkedInventory: BlockPos? = null
//    var portalDestination: PortalDestination? = null
    var owner: UUID? = null
//    var portal: Portal? = null
    var partner: SealBlockEntity? = null

    override fun tick() {
        if (world!!.isClient || !(cachedState.get(Properties.ENABLED) as Boolean)) return
        val combination = CombinationRegistry.registry[runes]
        if (combination != null && counter % combination.delay == 0)
            combination.tick(this)
        counter++
    }

    fun beforeRuneChange() {
        CombinationRegistry.registry[runes]?.beforeRuneChange(this)
    }

    fun afterRuneChange() {
        CombinationRegistry.registry[runes]?.afterRuneChange(this)
    }

    fun removePortal() {
//        this.portalDestination = null
//        this.portal?.remove(Entity.RemovalReason.DISCARDED)
//        this.portal = null
        this.partner = null
    }

//    fun findPartner() : SealBlockEntity? {
//        if (portalDestination == null) return partner
//        val found = MiscHelper.getServer().getWorld(portalDestination!!.world)?.getBlockEntity(portalDestination!!.pos) as? SealBlockEntity
//        return partner ?: found
//    }

    fun getDirection(): Direction = when(cachedState[WallMountedBlock.FACE]) {
        WallMountLocation.CEILING -> Direction.DOWN
        WallMountLocation.FLOOR -> Direction.UP
        else -> cachedState[WallMountedBlock.FACING]
    }

    fun getFacing(): Direction = cachedState[WallMountedBlock.FACING]

    override fun writeNbt(nbt: NbtCompound){
        nbt.putString("runes", runes)
        nbt.put("linkedInventory", linkedInventory?.toNbt() ?: NbtCompound())
//        nbt.put("portalDestination", portalDestination?.toNbt() ?: NbtCompound())
        nbt.putUuid("owner", owner)
        super.writeNbt(nbt)
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        runes = nbt.getString("runes")
        val linkedPos = nbt.getCompound("linkedInventory")
        if (!linkedPos.isEmpty) {
            linkedInventory = posFromNbt(linkedPos)
        }
//        val destination = nbt.getCompound("portalDestination")
//        if (!destination.isEmpty) {
//            portalDestination = PortalDestination.fromNbt(destination)
//        }
        owner = nbt.getUuid("owner")
    }

}