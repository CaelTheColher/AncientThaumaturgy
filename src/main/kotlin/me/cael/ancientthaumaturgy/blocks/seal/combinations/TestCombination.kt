package me.cael.ancientthaumaturgy.blocks.seal.combinations

import me.cael.ancientthaumaturgy.blocks.seal.SealBlockEntity
import net.minecraft.text.Text

class TestCombination : AbstractSealCombination(40) {
    override fun tick(seal: SealBlockEntity) {
//        entity.world?.players?.forEach { player -> player.sendMessage(Text.of("spam"), true) }
    }
}