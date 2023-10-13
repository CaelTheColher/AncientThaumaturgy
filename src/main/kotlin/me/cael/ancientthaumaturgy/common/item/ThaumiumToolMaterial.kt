package me.cael.ancientthaumaturgy.common.item

import net.fabricmc.yarn.constants.MiningLevels
import net.minecraft.item.ToolMaterial
import net.minecraft.recipe.Ingredient

class ThaumiumToolMaterial : ToolMaterial {
    override fun getDurability(): Int = 905

    override fun getMiningSpeedMultiplier(): Float = 6.0f

    override fun getAttackDamage(): Float = 2.0f

    override fun getMiningLevel(): Int = MiningLevels.IRON

    override fun getEnchantability(): Int = 25

    override fun getRepairIngredient(): Ingredient = Ingredient.ofItems(ItemCompendium.THAUMIUM_INGOT)
}