package org.moztw.bot.telegram.space.co2.model

data class Channel(
        val id: Int,
        val name: String,
        val description: String,
        val latitude: String,
        val longitude: String,
        val field1: String,
        val created_at: String,
        val updated_at: String,
        val last_entry_id: Int
)
