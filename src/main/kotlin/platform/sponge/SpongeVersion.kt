/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2023 minecraft-dev
 *
 * MIT License
 */

package com.demonwav.mcdev.platform.sponge

import com.demonwav.mcdev.creator.getText
import com.demonwav.mcdev.util.fromJson
import com.google.gson.Gson
import com.intellij.openapi.diagnostic.logger

data class SpongeVersion(var versions: LinkedHashMap<String, String>, var selectedIndex: Int) {
    companion object {
        private val LOGGER = logger<SpongeVersion>()

        suspend fun downloadData(): SpongeVersion? {
            return try {
                val text = getText("sponge_v2.json")
                Gson().fromJson(text, SpongeVersion::class)
            } catch (e: Exception) {
                LOGGER.error("Failed to download Sponge version json", e)
                null
            }
        }
    }
}
