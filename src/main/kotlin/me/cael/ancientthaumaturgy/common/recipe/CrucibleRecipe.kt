package me.cael.ancientthaumaturgy.common.recipe

import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import me.cael.ancientthaumaturgy.common.blockentity.CrucibleBlockEntity
import me.cael.ancientthaumaturgy.utils.identifier
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.network.PacketByteBuf
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.RecipeType
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import net.minecraft.world.World

class CrucibleRecipe(private val identifier: Identifier, private val group: String, val input: Ingredient, val visPerTick: Long, val smeltTime: Int) : Recipe<CrucibleBlockEntity> {

    override fun matches(inv: CrucibleBlockEntity, world: World): Boolean = input.test(inv.inventory[0])

    override fun craft(inv: CrucibleBlockEntity?): ItemStack = Items.AIR.defaultStack

    override fun fits(width: Int, height: Int): Boolean = false

    override fun getOutput(): ItemStack = Items.AIR.defaultStack

    override fun getId(): Identifier = identifier

    override fun getGroup(): String = group

    override fun getSerializer(): RecipeSerializer<*> = SERIALIZER

    override fun getType(): RecipeType<*> = TYPE

    companion object {
        val ID = identifier("crucible")
        val TYPE = object : RecipeType<CrucibleRecipe> {}
        val SERIALIZER = CrucibleRecipeSerializer()
    }

    class CrucibleRecipeSerializer : RecipeSerializer<CrucibleRecipe> {

        override fun read(id: Identifier, json: JsonObject): CrucibleRecipe {
            val string = JsonHelper.getString(json, "group", "")!!
            val ingredient = Ingredient.fromJson(JsonHelper.getObject(json, "ingredient"))
            val visPerTick = JsonHelper.getLong(json, "visPerTick")
            val smeltTime = JsonHelper.getInt(json, "smeltTime")
            return if (ingredient.isEmpty) {
                throw JsonParseException("No ingredients for shapeless recipe")
            } else {
                CrucibleRecipe(id, string, ingredient, visPerTick, smeltTime)
            }
        }

        override fun read(id: Identifier, buf: PacketByteBuf): CrucibleRecipe {
            val string: String = buf.readString(32767)
            val ingredient = Ingredient.fromPacket(buf)
            val visPerTick = buf.readVarLong()
            val smeltTime = buf.readVarInt()
            return CrucibleRecipe(id, string, ingredient, visPerTick, smeltTime)
        }

        override fun write(buf: PacketByteBuf, recipe: CrucibleRecipe) {
            buf.writeString(recipe.group)

            recipe.input.write(buf)

            buf.writeLong(recipe.visPerTick)
            buf.writeInt(recipe.smeltTime)
        }

    }

}