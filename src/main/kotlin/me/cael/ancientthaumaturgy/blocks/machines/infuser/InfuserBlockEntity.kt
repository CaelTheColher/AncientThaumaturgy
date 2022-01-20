package me.cael.ancientthaumaturgy.blocks.machines.infuser

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.recipe.RecipeInputProvider
import net.minecraft.recipe.RecipeMatcher
import net.minecraft.screen.PropertyDelegate
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class InfuserBlockEntity(type: BlockEntityType<*>?, pos: BlockPos, state: BlockState) : BlockEntity(type, pos, state), Inventory, RecipeInputProvider {

    var inventory: DefaultedList<ItemStack> = DefaultedList.ofSize(8, ItemStack.EMPTY)
    private var vis = 0
    private var visRequirement = 0

    val propertyDelegate = object: PropertyDelegate {
        override fun get(index: Int): Int {
            return when(index) {
                0 -> vis
                1 -> visRequirement
                else -> 0
            }
        }

        override fun set(index: Int, value: Int) {
            when(index) {
                0 -> vis = value
                1 -> visRequirement = value
            }
        }

        override fun size(): Int = 2
    }

    fun tick() {
        val match = world!!.recipeManager.getFirstMatch(InfuserRecipe.TYPE, this, world)
        if (match.isPresent) {
            val recipe = match.get()
            val result = recipe.output.copy()
            visRequirement = recipe.vis
            vis = visRequirement // debug
            if (inventory[6].count + result.count <= 64 && (inventory[6].isEmpty || inventory[6].isItemEqual(result)) && vis >= visRequirement) {

                recipe.input.forEach { ingredient ->
                    inventory.filter { ingredient.test(it) }.forEach { it.decrement(1) }
                }
                result.increment(inventory[6].count)
                vis -= visRequirement
                inventory[6] = result
            }
        }
    }

    override fun writeNbt(tag: NbtCompound){
        Inventories.writeNbt(tag, this.inventory)
        super.writeNbt(tag)
    }

    override fun readNbt(tag: NbtCompound) {
        super.readNbt(tag)
        this.inventory = DefaultedList.ofSize(size(), ItemStack.EMPTY)
        Inventories.readNbt(tag, this.inventory)
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