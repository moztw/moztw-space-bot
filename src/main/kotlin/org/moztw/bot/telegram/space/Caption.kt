package org.moztw.bot.telegram.space

import org.telegram.telegrambots.meta.api.methods.groupadministration.SetChatTitle

internal class Caption {
    private fun getCaption(chatId: Long) = SetChatTitle().apply { setChatId(chatId.toString()) }
    fun getCaptionOpened(chatId: Long) = getCaption(chatId).apply { title = "Moz://TW（工寮開放中）" }
    fun getCaptionClosed(chatId: Long) = getCaption(chatId).apply { title = "Moz://TW" }
}
