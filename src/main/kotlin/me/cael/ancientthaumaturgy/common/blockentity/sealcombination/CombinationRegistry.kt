package me.cael.ancientthaumaturgy.common.blockentity.sealcombination

import me.cael.ancientthaumaturgy.common.blockentity.sealcombination.earth.Break
import me.cael.ancientthaumaturgy.common.blockentity.sealcombination.earth.Decompose
import me.cael.ancientthaumaturgy.common.blockentity.sealcombination.earth.Place
import me.cael.ancientthaumaturgy.common.blockentity.sealcombination.earth.Smelt

object CombinationRegistry {
    val registry = HashMap<String, AbstractSealCombination>()

    fun registerCombinations() {
        // Air
        // Test
        registry["AAA"] = TestCombination()

        // Earth
        // Break Block
        registry["E"] = Break(0.0, 0.0)
        registry["EE"] = Break(1.0, 0.0)
        registry["EEE"] = Break(1.0, 1.0)
        // Place Block
        registry["EM"] = Place(0.0, 0.0)
        registry["EMM"] = Place(1.0, 0.0)
        registry["EME"] = Place(1.0, 1.0)
        // Decompose Block
        registry["EC"] = Decompose(0.0, 0.0)
        registry["ECC"] = Decompose(1.0, 0.0)
        registry["ECE"] = Decompose(1.0, 1.0)
        // Smelt Block
        registry["EF"] = Smelt(0.0, 0.0)
        registry["EFE"] = Smelt(1.0, 1.0)
        registry["EFF"] = Smelt(1.0, 0.0)
    }
}