package com.amshinski.miawiki.data

import com.google.gson.Gson
import net.fabricmc.loader.api.FabricLoader
import java.io.InputStreamReader

object MobDataLoader {
    private val gson = Gson()
    private val mobs: List<MobData> by lazy {
        val mobDataList = mutableListOf<MobData>()
        val mobFiles = FabricLoader.getInstance().getModContainer("miawiki").get().findPath("data/miawiki/mobs").get().toFile().listFiles()
        mobFiles?.forEach { file ->
            if (file.extension == "json") {
                val reader = InputStreamReader(file.inputStream())
                val mob = gson.fromJson(reader, MobData::class.java)
                mobDataList.add(mob)
                reader.close()
            }
        }
        mobDataList
    }

    fun loadMobs(): List<MobData> = mobs
}