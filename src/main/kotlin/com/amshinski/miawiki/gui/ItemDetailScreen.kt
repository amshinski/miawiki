package com.amshinski.miawiki.gui

import com.amshinski.miawiki.data.ItemData
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.Text

class ItemDetailScreen(
    private val item: ItemData,
    private val previousScreen: Screen
) : Screen(Text.literal(item.name)) {
    override fun init() {
        super.init()

        // Back Button
        addDrawableChild(
            ButtonWidget.builder(Text.literal("Back")) {
                client?.setScreen(previousScreen)
            }.dimensions(10, 10, 50, 20).build()
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

        // Display Item Image
        // TODO: Load and draw item.image

        // Display Recipes and How to Obtain
        var yPosition = 50
        context.drawTextWithShadow(textRenderer, "Recipes:", 20, yPosition.toInt(), 0xFFFFFF)
        yPosition += 15

        item.recipes.forEach { recipe ->
            val recipeText = "- Ingredients: ${recipe.ingredients.joinToString(", ")} -> Result: ${recipe.result}"
            context.drawTextWithShadow(textRenderer, recipeText, 30, yPosition, 0xFFFFFF)
            yPosition += 15
        }

        yPosition += 10
        context.drawTextWithShadow(textRenderer, "How to Obtain:", 20, yPosition, 0xFFFFFF)
        yPosition += 15

        item.obtainMethods.forEach { method ->
            context.drawTextWithShadow(textRenderer, "- $method", 30, yPosition, 0xFFFFFF)
            yPosition += 15
        }

        super.render(context, mouseX, mouseY, delta)
    }
}