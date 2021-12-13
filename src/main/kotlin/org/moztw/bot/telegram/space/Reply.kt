package org.moztw.bot.telegram.space

import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.User

internal class Reply {
    private fun getGeneralMessage(chatId: Long) = SendMessage().apply {
        setChatId(chatId.toString())
        parseMode = "Markdown"
    }

    fun getGeneralMessageOpen(chatId: Long, operator: User) =
        getGeneralMessage(chatId).apply {
            text = "#工寮開門 ${Bot.dateTime}（by [${operator.firstName}](tg://user?id=${operator.id})）"
        }

    fun getGeneralMessageClose(chatId: Long, operator: User) =
        getGeneralMessage(chatId).apply {
            text = "#工寮關門 ${Bot.dateTime}（by [${operator.firstName}](tg://user?id=${operator.id})）"
        }

    private fun getMessage(message: Message) = SendMessage().apply {
        chatId = message.chatId.toString()
        replyToMessageId = message.messageId
    }

    fun getMessageOpen(message: Message) = getMessage(message).apply {
        text = "#工寮開門 已於 ${Bot.dateTime} 送出開門資訊。\n\n" +
                "您可以先擦拭白板並書寫 Keyholder 名稱、活動名稱以及開關門預定時間。\n" +
                "招呼訪客時，請提醒他們於白板簽到（暱稱或 Mozillians ID e.g. moz:irvin）"
    }

    fun getMessageClose(message: Message) = getMessage(message).apply {
        text = "#工寮關門 已於 " + Bot.dateTime + " 送出關門資訊。\n\n" +
                "請檢視以下項目是否完成，完成後可點選來標記："
        replyMarkup = Keyboard().showButton
    }

    private fun getOtherMessage(chatId: Long) = SendMessage().apply {
        setChatId(chatId.toString())
        parseMode = "Markdown"
    }

    fun getOtherMessageOpen(message: Message, chatId: Long, operator: User) =
        getOtherMessage(chatId).apply {
            text = "#工寮開門 [${operator.firstName}](tg://user?id=${operator.id}) 已從「${message.chat.title}」群組於 ${Bot.dateTime} 送出開門資訊。"
        }

    fun getOtherMessageClose(message: Message, chatId: Long, operator: User) =
        getOtherMessage(chatId).apply {
            text = "#工寮關門 [${operator.firstName}](tg://user?id=${operator.id}) 已從「${message.chat.title}」群組於 ${Bot.dateTime} 送出關門資訊。"
        }

    fun getCheckList(query: CallbackQuery): EditMessageReplyMarkup? {
        val data = query.data.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        when (data[0]) {
            "hide" -> return EditMessageReplyMarkup().apply {
                chatId = query.message.chatId.toString()
                messageId = query.message.messageId
                replyMarkup = Keyboard().showButton
            }
            "list" -> return if (data.size < 2) null else EditMessageReplyMarkup().apply {
                chatId = query.message.chatId.toString()
                messageId = query.message.messageId
                replyMarkup = Keyboard().getCheckList(Integer.parseInt(data[1], 16))
            }
        }
        return null
    }
}
