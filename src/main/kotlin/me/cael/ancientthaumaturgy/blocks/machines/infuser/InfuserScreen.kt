package me.cael.ancientthaumaturgy.blocks.machines.infuser

import me.cael.ancientthaumaturgy.utils.identifier
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text

class InfuserScreen(handler: InfuserScreenHandler, inventory: PlayerInventory, title: Text) : HandledScreen<InfuserScreenHandler>(handler, inventory, title) {
    private val background = identifier("textures/gui/infuser.png")

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        this.renderBackground(matrices)
        super.render(matrices, mouseX, mouseY, delta)
        drawMouseoverTooltip(matrices, mouseX, mouseY)
    }

    override fun drawForeground(matrices: MatrixStack, mouseX: Int, mouseY: Int) {}

    override fun drawBackground(matrices: MatrixStack, delta: Float, mouseX: Int, mouseY: Int) {
        this.client!!.textureManager.bindTexture(background)
        this.drawTexture(matrices, this.x, (this.height - this.backgroundHeight-74) / 2, 0, 0, this.backgroundWidth, this.backgroundHeight+74)
    }
}
