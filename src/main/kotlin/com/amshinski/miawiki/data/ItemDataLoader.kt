package com.amshinski.miawiki.data

import com.google.gson.Gson
import net.fabricmc.loader.api.FabricLoader
import java.io.InputStreamReader

object ItemDataLoader {
    private val gson = Gson()
    private val items: List<ItemData> by lazy {
        val itemDataList = mutableListOf<ItemData>()
        val itemFiles = FabricLoader.getInstance().getModContainer("miawiki").get().findPath("data/miawiki/items").get().toFile().listFiles()
        itemFiles?.forEach { file ->
            if (file.extension == "json") {
                val reader = InputStreamReader(file.inputStream())
                val item = gson.fromJson(reader, ItemData::class.java)
                itemDataList.add(item)
                reader.close()
            }
        }
        itemDataList
    }

    fun getItemByName(name: String): ItemData? {
        return items.find { it.name.equals(name, ignoreCase = true) }
    }

    fun loadItems(): List<ItemData> = items
}