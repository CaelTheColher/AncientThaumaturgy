package me.cael.ancientthaumaturgy.blocks.seal

import me.cael.ancientthaumaturgy.blocks.seal.combinations.CombinationRegistry
import net.minecraft.block.BlockState
import net.minecraft.block.WallMountedBlock
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.block.enums.WallMountLocation
import net.minecraft.nbt.NbtCompound
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

class SealBlockEntity(type: BlockEntityType<*>?, pos: BlockPos, state: BlockState) : BlockEntity(type, pos, state) {
    var lastRenderDegree = 0f
    var runes = ""
    var counter = 0

    fun tick() {
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

    override fun writeNbt(tag: NbtCompound){
        tag.putString("runes", runes)
        super.writeNbt(tag)
    }

    override fun readNbt(tag: NbtCompound) {
        super.readNbt(tag)
        runes = tag.getString("runes")
    }

    companion object {
        @Suppress("unused_parameter")
        fun ticker(world: World, pos: BlockPos, state: BlockState, entity: SealBlockEntity) {
            if (!world.isClient) {
                entity.tick()
            }
        }
    }

}