package me.cael.ancientthaumaturgy.blocks.seal

import me.cael.ancientthaumaturgy.blocks.seal.combinations.CombinationRegistry
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable
import net.minecraft.block.BlockState
import net.minecraft.block.WallMountedBlock
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.block.enums.WallMountLocation
import net.minecraft.nbt.CompoundTag
import net.minecraft.state.property.Properties
import net.minecraft.util.Tickable
import net.minecraft.util.math.Direction

class SealBlockEntity(type: BlockEntityType<*>?) : BlockEntity(type), Tickable, BlockEntityClientSerializable{
    var lastRenderDegree = 0f
    var runes = ""
    var tick = 0

    override fun tick() {
        if (world!!.isClient || !(cachedState.get(Properties.ENABLED) as Boolean)) return
        val combination = CombinationRegistry.registry[runes]
        if (combination != null && tick % combination.delay == 0)
            combination.tick(this)
        tick++
    }

    fun getDirection(): Direction = when(cachedState[WallMountedBlock.FACE]) {
        WallMountLocation.CEILING -> Direction.DOWN
        WallMountLocation.FLOOR -> Direction.UP
        else -> cachedState[WallMountedBlock.FACING]
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        super.toTag(tag)
        tag.putString("runes", runes)
        return tag
    }

    override fun fromTag(state: BlockState, tag: CompoundTag) {
        super.fromTag(state, tag)
        runes = tag.getString("runes")
    }

    override fun fromClientTag(tag: CompoundTag) {
        runes = tag.getString("runes")
    }

    override fun toClientTag(tag: CompoundTag): CompoundTag {
        tag.putString("runes", runes)
        return tag
    }
}