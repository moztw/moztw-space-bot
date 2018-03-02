package org.moztw.bot.telegram.space

import org.telegram.telegrambots.api.methods.groupadministration.SetChatTitle

internal class Caption {
    private fun getCaption(chatId: Long) = SetChatTitle().setChatId(chatId)!!
    fun getCaptionOpened(chatId: Long) = getCaption(chatId).setTitle("Moz://TW（工寮開放中）")!!
    fun getCaptionClosed(chatId: Long) = getCaption(chatId).setTitle("Moz://TW")!!
}
