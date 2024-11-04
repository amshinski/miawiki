package com.amshinski.miawiki.gui

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.Text

class WikiScreen : Screen(Text.literal("MIA Wiki")) {
    override fun init() {
        val centerX = this.width / 2
        val centerY = this.height / 2

        // Add Category Buttons
        val buttonWidth = 100
        val buttonHeight = 20
        val spacing = 25

        addDrawableChild(
            ButtonWidget.builder(Text.literal("Mobs")) {
                client?.setScreen(MobsScreen())
            }.dimensions(centerX - buttonWidth / 2, centerY - spacing, buttonWidth, buttonHeight).build()
        )

        addDrawableChild(
            ButtonWidget.builder(Text.literal("Items")) {
//                client?.setScreen(ItemsScreen())
            }.dimensions(centerX - buttonWidth / 2, centerY, buttonWidth, buttonHeight).build()
        )

        addDrawableChild(
            ButtonWidget.builder(Text.literal("Ores")) {
//                client?.setScreen(OresScreen())
            }.dimensions(centerX - buttonWidth / 2, centerY + spacing, buttonWidth, buttonHeight).build()
        )

        // Close Button
        addDrawableChild(
            ButtonWidget.builder(Text.literal("X")) {
                client?.setScreen(null)
            }.dimensions(this.width - 25, 5, 20, 20).build()
        )
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        this.renderBackground(context, mouseX, mouseY, delta)
        context.drawCenteredTextWithShadow(textRenderer, title, this.width / 2, 15, 0xFFFFFF)
        super.render(context, mouseX, mouseY, delta)
    }
}