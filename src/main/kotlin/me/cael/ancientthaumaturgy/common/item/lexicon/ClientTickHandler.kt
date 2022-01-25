package me.cael.ancientthaumaturgy.common.item.lexicon

import net.minecraft.client.MinecraftClient

class ClientTickHandler {
    companion object {
        var ticksWithLexicaOpen = 0f
        var pageFlipTicks = 0f
        var ticksInGame = 0f
        var partialTicks = 0f
        var delta = 0f
        var total = 0f

        fun calcDelta() {
            val oldTotal = total
            total = ticksInGame + partialTicks
            delta = total - oldTotal
        }

        fun renderTick(renderTickTime: Float) {
            partialTicks = renderTickTime
        }

        fun clientTickEnd(mc: MinecraftClient) {
            if (!mc.isPaused) {
                ticksInGame++
                partialTicks = 0f
            }
            val ticksToOpen = 10f
            if (LexiconItem.isOpen) {
                if (ticksWithLexicaOpen < 0) {
                    ticksWithLexicaOpen = 0f
                }
                if (ticksWithLexicaOpen < ticksToOpen) {
                    ticksWithLexicaOpen++
                }
                if (pageFlipTicks > 0) {
                    pageFlipTicks--
                }
            } else {
                pageFlipTicks = 0f
                if (ticksWithLexicaOpen > 0) {
                    if (ticksWithLexicaOpen > ticksToOpen) {
                        ticksWithLexicaOpen = ticksToOpen
                    }
                    ticksWithLexicaOpen--
                }
            }
            calcDelta()
        }

        fun notifyPageChange() {
            if (pageFlipTicks == 0f) {
                pageFlipTicks = 5f
            }
        }
    }
}