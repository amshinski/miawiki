package com.amshinski.miawiki.gui

import com.amshinski.miawiki.data.MobData
import com.amshinski.miawiki.data.MobDataLoader
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.AbstractParentElement
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.Drawable
import net.minecraft.client.gui.Selectable
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.client.texture.NativeImage
import net.minecraft.resource.Resource
import java.util.Optional

class MobListWidget(
    private val client: MinecraftClient,
    private val width: Int,
    private val height: Int,
    private val top: Int,
    private val bottom: Int,
    private val previousScreen: Screen
) : AbstractParentElement(), Drawable, Selectable {

    private val allMobs: List<MobData> = MobDataLoader.loadMobs()
    private var scrollOffset = 0
    private val entryHeight = 80
    private var entries: List<MobEntryWidget>
    private val columns = 3
    private val margin = 20
    private val spacing = 10
    private val entryWidth = (width - margin * 2 - spacing * (columns - 1)) / columns

    init {
        entries = allMobs.map { mob ->
            MobEntryWidget(mob, previousScreen)
        }
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        enableScissor(0, top, client.window.scaledWidth.toInt(), bottom)

        // Start drawing entries
        var x = margin
        var y = top - scrollOffset
        var col = 0

        for (entry in entries) {
            // Set position and size for each entry
            entry.x = x
            entry.y = y
            entry.setWidth(entryWidth)
            entry.setHeight(entryHeight)

            // Only render entries within visible area
            if (y + entryHeight > top && y < bottom) {
                entry.render(context, mouseX, mouseY, delta)
            }

            // Move to next column
            col++
            if (col >= columns) {
                col = 0
                x = margin
                y += entryHeight + spacing
            } else {
                x += entryWidth + spacing
            }
        }

        disableScissor()
    }

    private fun enableScissor(x: Int, y: Int, width: Int, height: Int) {
        val scale = client.window.scaleFactor
        val framebufferHeight = client.window.framebufferHeight
        val xScissor = (x * scale).toInt()
        val yScissor = (framebufferHeight - (y + height) * scale).toInt()
        val widthScissor = (width * scale).toInt()
        val heightScissor = (height * scale).toInt()
        RenderSystem.enableScissor(xScissor, yScissor, widthScissor, heightScissor)
    }

    private fun disableScissor() {
        RenderSystem.disableScissor()
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (!isMouseOver(mouseX, mouseY)) {
            return false
        }
        for (entry in entries) {
            if (entry.mouseClicked(mouseX, mouseY, button)) {
                return true
            }
        }
        return false
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, horizontalAmount: Double, verticalAmount: Double): Boolean {
        val totalRows = (entries.size + columns - 1) / columns
        val contentHeight = totalRows * (entryHeight + spacing) - spacing
        val maxScroll = contentHeight - (bottom - top)
        scrollOffset = (scrollOffset - (verticalAmount * 20).toInt()).coerceIn(0, maxScroll.coerceAtLeast(0))
        return true
    }

    override fun isMouseOver(mouseX: Double, mouseY: Double): Boolean {
        return mouseX >= 0 && mouseX < width && mouseY >= top && mouseY < bottom
    }

    override fun appendNarrations(builder: NarrationMessageBuilder) {
        // Implement narration if needed
    }

    override fun getType(): Selectable.SelectionType {
        return Selectable.SelectionType.NONE
    }

    override fun children(): List<Element> {
        return entries
    }

    fun filter(text: String) {
        val filteredMobs = if (text.isEmpty()) {
            allMobs
        } else {
            allMobs.filter { it.name.contains(text, ignoreCase = true) }
        }
        // Recreate entries based on filtered mobs
        entries = filteredMobs.map { mob ->
            MobEntryWidget(mob, previousScreen)
        }
        // Reset scroll offset
        scrollOffset = 0
    }

    inner class MobEntryWidget(
        private val mob: MobData,
        private val previousScreen: Screen
    ) : ClickableWidget(0, 0, 0, 0, Text.empty()) {

        private val textureIdentifier: Identifier = Identifier("miawiki", "textures/gui/${mob.image}")
        private val textureSize = 64
        private val aspectRatio: Float
        private val dynamicWidth: Int

        init {
            var tempAspectRatio = 1f
            var tempDynamicWidth = textureSize
            val resourceOptional: Optional<Resource> = client.resourceManager.getResource(textureIdentifier)
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

        override fun renderWidget(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
            val actualWidth = (textureSize * aspectRatio).toInt()

            // Center the image horizontally within the widget
            val imgX = x + (width - actualWidth) / 2
            val imgY = y + 10

            // Draw background rectangle
            context.fill(imgX, imgY, imgX + actualWidth, imgY + textureSize, 0x0DCCCCCC.toInt())

            // Draw texture
            context.drawTexture(
                textureIdentifier,
                imgX,
                imgY,
                0f,
                0f,
                actualWidth,
                textureSize,
                actualWidth,
                textureSize
            )

            // Draw mob name centered below image
            val textRenderer: TextRenderer = client.textRenderer
            val nameWidth = textRenderer.getWidth(mob.name)
            val textX = x + (width - nameWidth) / 2
            val textY = imgY + textureSize + 5
            context.drawTextWithShadow(textRenderer, mob.name, textX, textY, 0xFFFFFF)
        }

        override fun onClick(mouseX: Double, mouseY: Double) {
            client.setScreen(MobDetailScreen(mob, previousScreen))
        }

        override fun appendClickableNarrations(builder: NarrationMessageBuilder?) {
            //
        }
    }
}
