package me.cael.ancientthaumaturgy.common.block

import me.cael.ancientthaumaturgy.common.blockentity.BlockEntityCompendium
import me.cael.ancientthaumaturgy.common.blockentity.PortableHoleBlockEntity
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.ShapeContext
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World

class PortableHoleBlock(settings: Settings) : BlockWithEntity(settings) {

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = PortableHoleBlockEntity(pos, state)
    override fun <T : BlockEntity> getTicker(world: World, state: BlockState, type: BlockEntityType<T>): BlockEntityTicker<T>? {
        return checkType(type, BlockEntityCompendium.PORTABLE_HOLE_BLOCK_TYPE,
            PortableHoleBlockEntity.Companion::ticker
        )
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun getOutlineShape(state: BlockState, view: BlockView?, pos: BlockPos?, context: ShapeContext?): VoxelShape = VoxelShapes.empty()

}