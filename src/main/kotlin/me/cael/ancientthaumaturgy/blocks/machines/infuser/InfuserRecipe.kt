package me.cael.ancientthaumaturgy.blocks.machines.infuser

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import me.cael.ancientthaumaturgy.utils.identifier
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.recipe.*
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import net.minecraft.util.collection.DefaultedList
import net.minecraft.world.World

class InfuserRecipe(private val identifier: Identifier, private val group: String, private val output: ItemStack, val input: DefaultedList<Ingredient>, val vis: Int) : Recipe<InfuserBlockEntity> {

    override fun matches(inv: InfuserBlockEntity, world: World): Boolean {
        val matches = input.filter { ingredient ->
            inv.inventory.stream().anyMatch { ingredient.test(it) }
        }
        return matches.size == input.size
    }

    override fun craft(inv: InfuserBlockEntity?): ItemStack = output.copy()

    override fun fits(width: Int, height: Int): Boolean = false

    override fun getOutput(): ItemStack = output

    override fun getId(): Identifier = identifier

    override fun getGroup(): String = group

    override fun getSerializer(): RecipeSerializer<*> = SERIALIZER

    override fun getType(): RecipeType<*> = TYPE

    companion object {
        val ID = identifier("infuser")
        val TYPE = object : RecipeType<InfuserRecipe> {}
        val SERIALIZER = InfuserRecipeSerializer()
    }

    class InfuserRecipeSerializer : RecipeSerializer<InfuserRecipe> {

        override fun read(id: Identifier, json: JsonObject): InfuserRecipe {
            val string = JsonHelper.getString(json, "group", "")!!
            val defaultedList = getIngredients(JsonHelper.getArray(json, "ingredients"))
            val vis = JsonHelper.getInt(json, "vis")
            return if (defaultedList.isEmpty()) {
                throw JsonParseException("No ingredients for shapeless recipe")
            } else if (defaultedList.size > 9) {
                throw JsonParseException("Too many ingredients for shapeless recipe")
            } else {
                val itemStack = ShapedRecipe.getItem(JsonHelper.getObject(json, "result")).defaultStack
                InfuserRecipe(id, string, itemStack, defaultedList, vis)
            }
        }

        private fun getIngredients(json: JsonArray): DefaultedList<Ingredient> {
            val defaultedList = DefaultedList.of<Ingredient>()
            for (i in 0 until json.size()) {
                val ingredient = Ingredient.fromJson(json[i])
                if (!ingredient.isEmpty) {
                    defaultedList.add(ingredient)
                }
            }
            return defaultedList
        }

        override fun read(id: Identifier, buf: PacketByteBuf): InfuserRecipe {
            val string: String = buf.readString(32767)
            val i: Int = buf.readVarInt()
            val defaultedList = DefaultedList.ofSize(i, Ingredient.EMPTY)
            val vis = buf.readVarInt()
            for (j in defaultedList.indices) {
                defaultedList[j] = Ingredient.fromPacket(buf)
            }

            val itemStack: ItemStack = buf.readItemStack()
            return InfuserRecipe(id, string, itemStack, defaultedList, vis)
        }

        override fun write(buf: PacketByteBuf, recipe: InfuserRecipe) {
            buf.writeString(recipe.group)
            buf.writeVarInt(recipe.input.size)

            recipe.input.iterator().forEach {
                it.write(buf)
            }

            buf.writeInt(recipe.vis)
            buf.writeItemStack(recipe.output)
        }

    }

}