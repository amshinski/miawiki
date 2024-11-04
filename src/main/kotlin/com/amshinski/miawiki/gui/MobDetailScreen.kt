package com.amshinski.miawiki.gui

import com.amshinski.miawiki.data.ItemDataLoader
import com.amshinski.miawiki.data.MobData
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.texture.NativeImage
import net.minecraft.resource.Resource
import net.minecraft.util.Identifier
import net.minecraft.text.Text
import java.util.Optional

class MobDetailScreen(
    private val mob: MobData,
    private val previousScreen: Screen
) : Screen(Text.literal(mob.name)) {

    val textureIdentifier = Identifier("miawiki", "textures/gui/${mob.image}")
    val textureSize = 64
    private var aspectRatio: Float = 1f
    private var dynamicWidth: Int = textureSize

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

        var tempAspectRatio = 1f
        var tempDynamicWidth = textureSize
        val resourceOptional: Optional<Resource> = client!!.resourceManager.getResource(textureIdentifier)
        if (resourceOptional.isPresent) {
            val resource = resourceOptional.get()
            resource.inputStream.use { inputStream ->
                val image = NativeImage.read(inputStream)
                val textureOriginalWidth = image.width
                val textureOriginalHeight = image.height
                image.close()
                tempAspectRatio = textureOriginalWidth.toFloat() / textureOriginalHeight.toFloat()
                tempDynamicWidth = (textureSize * tempAspectRatio).toInt()
            }
        }
        aspectRatio = tempAspectRatio
        dynamicWidth = tempDynamicWidth
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        this.renderBackground(context, mouseX, mouseY, delta)

        super.render(context, mouseX, mouseY, delta)

        context.drawCenteredTextWithShadow(textRenderer, title, this.width / 2, 15, 0xFFFFFF)

        // Position to draw the image (centered horizontally)
        val actualWidth = (textureSize * aspectRatio).toInt()
        val x = (this.width - actualWidth) / 2
        val y = 40

        context.drawTexture(textureIdentifier, x, y, 0f, 0f, actualWidth, textureSize, actualWidth, textureSize)

        context.drawTextWithShadow(textRenderer, "Health: ${mob.health}", 20, 60, 0xFFFFFF)
        context.drawTextWithShadow(textRenderer, "Damage: ${mob.damage}", 20, 75, 0xFFFFFF)

        // Display Drops
        context.drawTextWithShadow(textRenderer, "Drops:", 20, 105, 0xFFFFFF)

        var yPosition = 120
        mob.drops.forEach { drop ->
            val dropText = "- ${drop.item} x${drop.quantity}${drop.chance?.let { " (${it * 100}%)" } ?: ""}"
            val textX = 30
            val textY = yPosition
            context.drawTextWithShadow(textRenderer, dropText, textX, textY, 0xFFFFFF)

            // Check if mouse is over the text
            val textWidth = textRenderer.getWidth(dropText)
            if (mouseX in textX..(textX + textWidth) && mouseY in textY..(textY + 10)) {
                // Render tooltip
                if (drop.item != "exp") {
                    context.drawTooltip(textRenderer, Text.literal("Click to view item details"), mouseX, mouseY)
                }
            }
            yPosition += 15
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        var yPosition = 95f
        mob.drops.forEach { drop ->
            val dropText = "- ${drop.item} x${drop.quantity}${drop.chance?.let { " (${it * 100}%)" } ?: ""}"
            val textX = 30.0
            val textY = yPosition.toDouble()
            val textWidth = textRenderer.getWidth(dropText).toDouble()

            if (mouseX in textX..(textX + textWidth) && mouseY in textY..(textY + 10)) {
                // Open Item Detail Screen
                // todo
                val itemData = ItemDataLoader.getItemByName(drop.item)
                if (itemData != null) {
//                    client?.setScreen(ItemDetailScreen(itemData, this))
                } else {
                    // Handle item not found
                }
                return true
            }
            yPosition += 15
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }
}