package me.cael.ancientthaumaturgy.common.blockentity

import me.cael.ancientthaumaturgy.common.blockentity.sealcombination.CombinationRegistry
import net.minecraft.block.BlockState
import net.minecraft.block.WallMountedBlock
import net.minecraft.block.enums.WallMountLocation
import net.minecraft.nbt.NbtCompound
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

class SealBlockEntity(pos: BlockPos, state: BlockState) : MachineEntity(BlockEntityCompendium.SEAL_BLOCK_TYPE, pos, state) {
    var lastRenderDegree = 0f
    var runes = ""
    var counter = 0
    var linkedInventory: BlockPos? = null

    override fun tick() {
        if (world!!.isClient || !(cachedState.get(Properties.ENABLED) as Boolean)) return
        val combination = CombinationRegistry.registry[runes]
        if (combination != null && counter % combination.delay == 0)
            combination.tick(this)
        counter++
    }

    fun getDirection(): Direction = when(cachedState[WallMountedBlock.FACE]) {
        WallMountLocation.CEILING -> Direction.DOWN
        WallMountLocation.FLOOR -> Direction.UP
        else -> cachedState[WallMountedBlock.FACING]
    }

    override fun writeNbt(nbt: NbtCompound){
        nbt.putString("runes", runes)
        val linkedPos = NbtCompound()
        if (linkedInventory != null) {
            linkedPos.putInt("x", linkedInventory!!.x)
            linkedPos.putInt("y", linkedInventory!!.y)
            linkedPos.putInt("z", linkedInventory!!.z)
        }
        nbt.put("linkedInventory", linkedPos)
        super.writeNbt(nbt)
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        runes = nbt.getString("runes")
        val linkedPos = nbt.getCompound("linkedInventory")
        if (!linkedPos.isEmpty) {
            linkedInventory = posFromNbt(linkedPos)
        }
    }

}