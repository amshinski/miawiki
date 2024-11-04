package com.amshinski.miawiki.keybinds

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW

object KeyBindings {
    lateinit var openWikiKey: KeyBinding

    fun register() {
        openWikiKey = KeyBindingHelper.registerKeyBinding(
            KeyBinding(
                "key.miawiki.open_wiki",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                "category.miawiki"
            )
        )
    }
}
