package me.cael.ancientthaumaturgy.client.screen

import me.cael.ancientthaumaturgy.common.container.InfuserScreenHandler
import me.cael.ancientthaumaturgy.utils.identifier
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text

class InfuserScreen(handler: InfuserScreenHandler, inventory: PlayerInventory, title: Text) : HandledScreen<InfuserScreenHandler>(handler, inventory, title) {
    private val background = identifier("textures/gui/infuser.png")

    init {
        backgroundHeight = 240
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        this.renderBackground(context)
        super.render(context, mouseX, mouseY, delta)
        super.drawMouseoverTooltip(context, mouseX, mouseY)
    }

    override fun drawForeground(context: DrawContext, mouseX: Int, mouseY: Int) {}

    override fun drawBackground(context: DrawContext, delta: Float, mouseX: Int, mouseY: Int) {
        context.drawTexture(background, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight)
        val progress = handler.getInfusionProgress()
        context.drawTexture(background, this.x+148, this.y+90+39-progress, 184, 39-progress, 7, 39)
    }
}
