package me.cael.ancientthaumaturgy.blocks.seal.combinations

import me.cael.ancientthaumaturgy.blocks.seal.combinations.earth.*

object CombinationRegistry {
    val registry = HashMap<String, AbstractSealCombination>()

    fun registerCombinations() {
        // Air
        // Test
        registry["AAA"] = TestCombination()

        // Earth
        // Break Block
        registry["E"] = BreakCombination(0.0, 0.0)
        registry["EE"] = BreakCombination(1.0, 0.0)
        registry["EEE"] = BreakCombination(1.0, 1.0)
        // Place Block
        registry["EM"] = PlaceCombination(0.0, 0.0)
        registry["EMM"] = PlaceCombination(1.0, 0.0)
        registry["EME"] = PlaceCombination(1.0, 1.0)
    }
}