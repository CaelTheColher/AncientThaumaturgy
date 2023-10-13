package me.cael.ancientthaumaturgy.common.blockentity.sealcombination

import me.cael.ancientthaumaturgy.common.blockentity.sealcombination.earth.*
import me.cael.ancientthaumaturgy.common.blockentity.sealcombination.magic.portal.Portal

object CombinationRegistry {
    val registry = HashMap<String, AbstractSealCombination>()

    fun registerCombinations() {

        /*
        AIR: Displaces
        EARTH: Affects blocks/the world?
        FIRE: Damage/Burning
        WATER: Healing?
        MAGIC: M A G I C
        CORRUPTION: Degradation

        Primary Element: the main effect (i.e. affect a block/the world)
        Secondary Element: the variation (i.e. break a block)
        Tertiary Element: the modifier (i.e. bigger range, faster operation time)

         */

        // Air
        // Test
        registry["AAA"] = TestCombination()

        //TODO: Make seals give observer signals on action

        // Earth
        // Break Block || TODO: Check the interaction with crops
        registry["E"] = Break(0.0, 0.0)
        registry["EE"] = Break(1.0, 0.0)
        registry["EEE"] = Break(1.0, 1.0)
        // Place Block || TODO: Make this also replant seeds
        registry["EM"] = Place(0.0, 0.0)
        registry["EMM"] = Place(1.0, 0.0)
        registry["EME"] = Place(1.0, 1.0)
        // Decompose Block
        registry["EC"] = Decompose(0.0, 0.0)
        registry["ECC"] = Decompose(1.0, 0.0)
        registry["ECE"] = Decompose(1.0, 1.0)
        // Smelt Block
        registry["EF"] = Smelt(0.0, 0.0)
        registry["EFF"] = Smelt(1.0, 0.0)
        registry["EFE"] = Smelt(1.0, 1.0)
        // Create ores from stone/air? Orechid much
        registry["EW"] = CreateOre(0.0, 0.0)
        registry["EWW"] = CreateOre(1.0, 0.0)
        registry["EWE"] = CreateOre(1.0, 1.0)
        // IDK
        registry["EA"]

        // Magic
        // Portal
//        registry["MA"]  = Portal('')
        registry["MAA"] = Portal('A')
        registry["MAE"] = Portal('E')
        registry["MAF"] = Portal('F')
        registry["MAW"] = Portal('W')
        registry["MAM"] = Portal('M')
        registry["MAC"] = Portal('C')


    }
}