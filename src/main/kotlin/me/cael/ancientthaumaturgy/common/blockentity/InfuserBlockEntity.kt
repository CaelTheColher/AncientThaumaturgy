package me.cael.ancientthaumaturgy.common.blockentity

import me.cael.ancientthaumaturgy.common.recipe.InfuserRecipe
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.recipe.RecipeInputProvider
import net.minecraft.recipe.RecipeMatcher
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class InfuserBlockEntity(pos: BlockPos, state: BlockState) : MachineEntity(BlockEntityCompendium.INFUSER_BLOCK_TYPE, pos, state, 1000, 100), Inventory, RecipeInputProvider {

    var inventory: DefaultedList<ItemStack> = DefaultedList.ofSize(8, ItemStack.EMPTY)
    var visRequirement = 0
    var ticks = 0
    var infuseTime = 0

    fun tick() {
        val match = world!!.recipeManager.getFirstMatch(InfuserRecipe.TYPE, this, world)
        if (match.isPresent) {
            val recipe = match.get()
            val result = recipe.output.copy()
            visRequirement = recipe.vis
            infuseTime = recipe.infuseTime
            //vis = visRequirement // debug
            if (inventory[6].count + result.count <= 64 && (inventory[6].isEmpty || inventory[6].isItemEqual(result)) && visStorage.amount >= visRequirement) {
                ticks++
                if (ticks >= infuseTime) {
                    recipe.input.forEach { ingredient ->
                        inventory.filter { ingredient.test(it) }.forEach { it.decrement(1) }
                    }
                    result.increment(inventory[6].count)
                    visStorage.amount -= visRequirement
                    inventory[6] = result
                    ticks = 0
                    infuseTime = 0
                    markDirtyAndSync()
                }
            }
        }
    }

    override fun writeNbt(nbt: NbtCompound){
        Inventories.writeNbt(nbt, this.inventory)
        super.writeNbt(nbt)
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        this.inventory = DefaultedList.ofSize(size(), ItemStack.EMPTY)
        Inventories.readNbt(nbt, this.inventory)
    }

    override fun clear() = inventory.clear()

    override fun size(): Int = inventory.size

    override fun isEmpty(): Boolean = inventory.all{ it.isEmpty }

    override fun getStack(slot: Int): ItemStack = inventory[slot]

    override fun removeStack(slot: Int, amount: Int): ItemStack = Inventories.splitStack(inventory, slot, amount)

    override fun removeStack(slot: Int): ItemStack = Inventories.removeStack(inventory, slot)

    override fun setStack(slot: Int, stack: ItemStack?) {
        inventory[slot] = stack
        if (stack!!.count > maxCountPerStack) {
            stack.count = maxCountPerStack
        }
    }

    override fun canPlayerUse(player: PlayerEntity?): Boolean {
        return if (world!!.getBlockEntity(pos) != this) {
            false
        } else {
            player!!.squaredDistanceTo(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5) <= 64.0
        }
    }

    override fun provideRecipeInputs(matcher: RecipeMatcher) {
        inventory.forEach {
            matcher.addInput(it)
        }
    }

    companion object {
        @Suppress("unused_parameter")
        fun ticker(world: World, pos: BlockPos, state: BlockState, entity: InfuserBlockEntity) {
            if (!world.isClient) {
                entity.tick()
            }
        }
    }
}