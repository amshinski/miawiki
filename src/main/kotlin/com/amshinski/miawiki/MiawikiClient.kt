package com.amshinski.miawiki

import com.amshinski.miawiki.gui.WikiScreen
import com.amshinski.miawiki.keybinds.KeyBindings
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents

object MiawikiClient : ClientModInitializer {
    override fun onInitializeClient() {
        KeyBindings.register()

        ClientTickEvents.END_CLIENT_TICK.register { client ->
            if (KeyBindings.openWikiKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(WikiScreen())
                } else if (client.currentScreen is WikiScreen) {
                    client.setScreen(null)
                }
            }
        }
    }
}