package org.moztw.bot.telegram.space

import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup
import org.telegram.telegrambots.api.objects.CallbackQuery
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.api.objects.User

internal class Reply {
    fun getGeneralMessageOpen(chatId: Long, operator: User) =
            SendMessage().setChatId(chatId).setText("#工寮開門 ${Bot.dateTime}（by [${operator.firstName}](tg://user?id=${operator.id})）")!!

    fun getGeneralMessageClose(chatId: Long, operator: User) =
            SendMessage().setChatId(chatId).setText("#工寮關門 ${Bot.dateTime}（by [${operator.firstName}](tg://user?id=${operator.id})）")!!

    private fun getMessage(message: Message) = SendMessage().setChatId(message.chatId!!).setReplyToMessageId(message.messageId)

    fun getMessageOpen(message: Message) = getMessage(message).setText(
            "#工寮開門 已於 ${Bot.dateTime} 送出開門資訊。\n\n" +
                    "您可以先擦拭白板並書寫 Keyholder 名稱、活動名稱以及開關門預定時間。\n" +
                    "招呼訪客時，請提醒他們於白板簽到（暱稱或 Mozillians ID e.g. moz:irvin）"
    )!!

    fun getMessageClose(message: Message) = getMessage(message).setText(
            "#工寮關門 已於 " + Bot.dateTime + " 送出關門資訊。\n\n" +
                    "請檢視以下項目是否完成，完成後可點選來標記："
    ).setReplyMarkup(Keyboard().showButton)!!

    private fun getOtherMessage(chatId: Long) = SendMessage().setChatId(chatId)

    fun getOtherMessageOpen(message: Message, chatId: Long, operator: User) =
            getOtherMessage(chatId).setText("#工寮開門 [${operator.firstName}](tg://user?id=${operator.id}) 已從「${message.chat.title}」群組於 ${Bot.dateTime} 送出開門資訊。")!!

    fun getOtherMessageClose(message: Message, chatId: Long, operator: User) =
            getOtherMessage(chatId).setText("#工寮關門 [${operator.firstName}](tg://user?id=${operator.id}) 已從「${message.chat.title}」群組於 ${Bot.dateTime} 送出關門資訊。")!!

    fun getCheckList(query: CallbackQuery): EditMessageReplyMarkup? {
        val data = query.data.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        when (data[0]) {
            "hide" -> return EditMessageReplyMarkup()
                    .setChatId(query.message.chatId!!)
                    .setMessageId(query.message.messageId)
                    .setReplyMarkup(Keyboard().showButton)
            "list" -> {
                return if (data.size < 2) null else EditMessageReplyMarkup()
                        .setChatId(query.message.chatId!!)
                        .setMessageId(query.message.messageId)
                        .setReplyMarkup(Keyboard().getCheckList(Integer.parseInt(data[1], 16)))
            }
        }
        return null
    }
}
