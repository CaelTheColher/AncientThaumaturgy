package me.cael.ancientthaumaturgy.common.blockentity

import me.cael.ancientthaumaturgy.common.block.BlockCompendium
import me.cael.ancientthaumaturgy.common.block.TankBlock
import me.cael.ancientthaumaturgy.utils.RegistryCompendium
import me.cael.ancientthaumaturgy.vis.api.EnergyStorage
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.registry.Registry

object BlockEntityCompendium: RegistryCompendium<BlockEntityType<*>>(Registry.BLOCK_ENTITY_TYPE) {

    val TANK_BLOCK_TYPE = register("tank_block", BlockEntityType.Builder.create(::TankBlockEntity, BlockCompendium.TANK_BLOCK).build(null)) as BlockEntityType<TankBlockEntity>
    val SEAL_BLOCK_TYPE = register("seal_block", BlockEntityType.Builder.create(::SealBlockEntity, BlockCompendium.SEAL_BLOCK).build(null)) as BlockEntityType<SealBlockEntity>
    val TUBE_BLOCK_TYPE = register("tube_block", BlockEntityType.Builder.create(::TubeBlockEntity, BlockCompendium.TUBE_BLOCK).build(null)) as BlockEntityType<TubeBlockEntity>
    val INFUSER_BLOCK_TYPE = register("infuser_block", BlockEntityType.Builder.create(::InfuserBlockEntity, BlockCompendium.INFUSER_BLOCK).build(null)) as BlockEntityType<InfuserBlockEntity>
    val CRUCIBLE_BLOCK_TYPE = register("crucible_block", BlockEntityType.Builder.create(::CrucibleBlockEntity, BlockCompendium.CRUCIBLE_BLOCK).build(null)) as BlockEntityType<CrucibleBlockEntity>

    init {
        EnergyStorage.SIDED.registerForBlockEntities({ be, _ -> (be as MachineEntity).visStorage }, SEAL_BLOCK_TYPE, TUBE_BLOCK_TYPE, INFUSER_BLOCK_TYPE, CRUCIBLE_BLOCK_TYPE)
        EnergyStorage.SIDED.registerForBlockEntity({be, _ ->
            val combinedStorage = TankBlockEntity.CombinedTankStorage()
            TankBlock.findAllTanks(be.world!!.getChunk(be.pos), be.world!!.getBlockState(be.pos), be.pos, mutableSetOf(), combinedStorage)
            combinedStorage
        }, TANK_BLOCK_TYPE)
    }

}