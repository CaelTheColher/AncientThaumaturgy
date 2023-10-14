package me.cael.ancientthaumaturgy.common.blockentity.sealcombination.earth

import me.cael.ancientthaumaturgy.common.blockentity.SealBlockEntity
import me.cael.ancientthaumaturgy.common.blockentity.sealcombination.AbstractSealCombination
import me.cael.ancientthaumaturgy.utils.forEach
import net.minecraft.block.Blocks
import net.minecraft.entity.ItemEntity
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.recipe.RecipeType
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class Smelt(range: Double, depth: Double) : AbstractSealCombination(10, range, depth) {
    override fun tick(seal: SealBlockEntity) {
        val area = getArea(seal.pos, seal.getDirection())
        area.forEach{ x,y,z ->
            val pos = BlockPos(x,y,z)
            val world = seal.world!!
            if (trySmelt(world, pos)) return
        }
    }

    // TODO: Remake this using a fakeplayer to avoid bypassing protections in multiplayer
    private fun trySmelt(world: World, pos: BlockPos) : Boolean {
        val state = world.getBlockState(pos)
        val smeltResult = getSmeltingResult(state.block.asItem(), world) ?: return false
        val block = if (smeltResult.item is BlockItem) (smeltResult.item as BlockItem).block else Blocks.AIR
        if (world.setBlockState(pos, block.defaultState)) {
            world.playSound(null, pos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 1.0f, 1.0f)
            if (smeltResult.item !is BlockItem) {
                world.spawnEntity(ItemEntity(world, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), smeltResult))
            }
            return true
        }
        return false
    }

    // should probably be changed for some config
    private fun getSmeltingResult(item: Item, world: World) : ItemStack? {
        val recipe = world.recipeManager.getFirstMatch(RecipeType.SMELTING, SimpleInventory(item.defaultStack), world)
        if (!recipe.isPresent) return null
        return recipe.get().getOutput(world.registryManager).copy()
    }

}