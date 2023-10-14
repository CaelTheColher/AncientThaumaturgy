package me.cael.ancientthaumaturgy.common.blockentity.sealcombination.earth

import me.cael.ancientthaumaturgy.common.blockentity.SealBlockEntity
import me.cael.ancientthaumaturgy.common.blockentity.sealcombination.AbstractSealCombination
import me.cael.ancientthaumaturgy.mixin.AccessorLootContextTypes
import me.cael.ancientthaumaturgy.utils.forEach
import me.cael.ancientthaumaturgy.utils.identifier
import net.minecraft.block.Block
import net.minecraft.item.BlockItem
import net.minecraft.loot.context.LootContextParameterSet
import net.minecraft.loot.context.LootContextType
import net.minecraft.registry.tag.BlockTags
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class CreateOre(range: Double, depth: Double) : AbstractSealCombination(25, range, depth) {

    override fun tick(seal: SealBlockEntity) {
        val area = getArea(seal.pos, seal.getDirection())

        val posList = ArrayList<BlockPos>()
        area.forEach { x, y, z ->
            posList.add(BlockPos(x, y, z))
        }

        val world = seal.world!!

        while (posList.isNotEmpty()) {
            val pos = posList.random()
            if (tryPlace(world, pos, getOre(world as ServerWorld))) {
                break
            }
            posList.remove(pos)
        }

    }

    // TODO: Remake this using a fakeplayer to avoid bypassing protections in multiplayer
    private fun tryPlace(world: World, pos: BlockPos, block: Block) : Boolean {
        val state = world.getBlockState(pos)
        val canPlace = (state.isIn(BlockTags.BASE_STONE_OVERWORLD) && block.defaultState.canPlaceAt(world, pos))
        if (canPlace && world.setBlockState(pos, block.defaultState)) {
            val soundGroup = block.defaultState.soundGroup
            world.playSound(null, pos, soundGroup.placeSound, SoundCategory.BLOCKS, soundGroup.volume, soundGroup.pitch)
            return true
        }
        return false
    }

    companion object {
        val ORE_SEAL_LOOT_CONTEXT: LootContextType = LootContextType.Builder().build()
        init {
            AccessorLootContextTypes.getMap()[identifier("ore_seal")] = ORE_SEAL_LOOT_CONTEXT
        }

        fun getOre(world: ServerWorld): Block {
            val table = world.server.lootManager.getLootTable(identifier("seal/ore/default"))

            val ctx = LootContextParameterSet.Builder(world).build(ORE_SEAL_LOOT_CONTEXT)
            val stackList = table.generateLoot(ctx)
            return ((stackList[0].item) as BlockItem).block
        }
    }

}