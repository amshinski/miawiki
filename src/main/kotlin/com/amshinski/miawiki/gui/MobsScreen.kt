package com.amshinski.miawiki.gui

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.text.Text

class MobsScreen : Screen(Text.literal("Mobs")) {
    private lateinit var searchField: TextFieldWidget
    private lateinit var mobListWidget: MobListWidget

    override fun init() {
        super.init()

        // Mob List
        mobListWidget = MobListWidget(client!!, this.width, this.height, 65, this.height - 30, MobsScreen())
        addDrawableChild(mobListWidget)

        // Back Button
        addDrawableChild(
            ButtonWidget.builder(Text.literal("Back")) {
                client?.setScreen(WikiScreen())
            }.dimensions(10, 10, 50, 20).build()
        )

        // Close Button
        addDrawableChild(
            ButtonWidget.builder(Text.literal("X")) {
                client?.setScreen(null)
            }.dimensions(this.width - 25, 5, 20, 20).build()
        )

        // Search Bar
        searchField = TextFieldWidget(textRenderer, this.width / 2 - 100, 40, 200, 20, Text.literal("Search"))
        searchField.setChangedListener { text ->
            mobListWidget.filter(text)
        }
        addDrawableChild(searchField)
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        this.renderBackground(context, mouseX, mouseY, delta)

        context.drawCenteredTextWithShadow(textRenderer, title, this.width / 2, 15, 0xFFFFFF)

        super.render(context, mouseX, mouseY, delta)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, horizontalAmount: Double, verticalAmount: Double): Boolean {
        if (mobListWidget.isMouseOver(mouseX, mouseY)) {
            return mobListWidget.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)
    }
}