package org.moztw.bot.telegram.space

import io.prometheus.client.Counter
import io.prometheus.client.Gauge
import okhttp3.OkHttpClient
import okhttp3.Request
import org.moztw.bot.telegram.space.co2.SpaceCo2
import org.telegram.telegrambots.meta.api.methods.ActionType
import org.telegram.telegrambots.meta.api.methods.groupadministration.SetChatTitle
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.*
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

internal class Bot(val username: String, val token: String) : TelegramLongPollingBot() {

    override fun onUpdateReceived(update: Update) {
        updates.inc()
        println("* [$dateTime] Update received: $update")

        if (update.hasMessage() && onMessageReceived(update.message)) return
        if (update.hasCallbackQuery() && onCallbackQueryReceived((update.callbackQuery))) return

        updatesUnhandled.inc()
        println("* [$dateTime] Update not handled: $update")
    }

    private fun onCallbackQueryReceived(callbackQuery: CallbackQuery): Boolean {
        callbackQueryCalls.inc()
        try {
            Reply().getCheckList(callbackQuery).run {
                telegramApiCalls.labels("EditMessageReplyMarkup").inc()
                execute(this)
            }
            return true
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }

        return false
    }

    private fun onMessageReceived(message: Message): Boolean {
        if (message.hasText()) {
            if (isCommandCO2(message.text)) {
                commandCalls.labels("co2").inc()
                try {
                    telegramApiCalls.labels("SendChatAction:Typing").inc()
                    execute(SendChatAction().apply {
                        chatId = message.chat.id.toString()
                        setAction(ActionType.TYPING)
                    })
                } catch (e: TelegramApiException) {
                    e.printStackTrace()
                }

                var messageText = "二氧化碳含量取得失敗，本服務暫時無法使用。"
                var imageUrl = ""
                try {
                    SpaceCo2().fetchCo2()?.let { co2 ->
                        val last = co2.feeds.last()
                        val lastUpdate = ZonedDateTime
                                .parse(last.created_at)
                                .withZoneSameInstant(ZoneId.of("Asia/Taipei"))
                                .format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))
                        messageText = "目前的二氧化碳含量為 <code>${last.field1}</code> ppm\n" +
                                "更新日期：<code>$lastUpdate</code>"
                        imageUrl = SpaceCo2().chartPng(co2)
                    }
                } finally {
                    if (imageUrl.isEmpty()) {
                        tryExecute(SendMessage().apply {
                            chatId = message.chat.id.toString()
                            replyToMessageId = message.messageId
                            text = messageText
                            parseMode = "HTML"
                        })
                    } else {
                        try {
                            telegramApiCalls.labels("SendChatAction:UploadPhoto").inc()
                            execute(SendChatAction().apply {
                                chatId = message.chat.id.toString()
                                setAction(ActionType.UPLOADPHOTO)
                            })

                            execute(SendPhoto().apply {
                                caption = messageText
                                chatId = message.chat.id.toString()
                                parseMode = "HTML"
                                replyToMessageId = message.messageId
                                photo = InputFile(imageUrl.let {
                                    OkHttpClient()
                                        .newCall(Request.Builder().url(it).build())
                                        .execute().body?.byteStream()
                                }, "moztw-space-co2.png")
                            })
                        } catch (e: TelegramApiException) {
                            e.printStackTrace()
                        }
                    }
                }
                return true
            } else if (isAdminChat(message.chat)) {
                if (isCommandOpen(message.text)) {
                    commandCalls.labels("space_open").inc()
                    spaceDoorState.set(1.0)
                    spaceDoorStateTime.setToCurrentTime()
                    if (tryExecute(Caption().getCaptionOpened(chatId = generalChatId))
                            && tryExecute(Reply().getGeneralMessageOpen(chatId = generalChatId, operator = message.from))
                            && tryExecute(Reply().getMessageOpen(message = message))) {
                        for (chatId in adminChats)
                            if (chatId != message.chatId)
                                if (!tryExecute(Reply().getOtherMessageOpen(message = message, chatId = chatId, operator = message.from)))
                                    return false
                        return true
                    }
                } else if (isCommandClose(message.text)) {
                    commandCalls.labels("space_close").inc()
                    spaceDoorState.set(0.0)
                    spaceDoorStateTime.setToCurrentTime()
                    if (tryExecute(Caption().getCaptionClosed(chatId = generalChatId))
                            && tryExecute(Reply().getGeneralMessageClose(chatId = generalChatId, operator = message.from))
                            && tryExecute(Reply().getMessageClose(message = message))) {
                        for (chatId in adminChats)
                            if (chatId != message.chatId)
                                if (!tryExecute(Reply().getOtherMessageClose(message = message, chatId = chatId, operator = message.from)))
                                    return false
                        return true
                    }
                }
            }
        }

        return false
    }

    private fun tryExecute(caption: SetChatTitle): Boolean {
        try {
            telegramApiCalls.labels("SetChatTitle").inc()
            execute(caption)
            return true
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }

        return false
    }

    private fun tryExecute(sendMessage: SendMessage): Boolean {
        try {
            telegramApiCalls.labels("SendMessage").inc()
            execute(sendMessage)
            return true
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }

        return false
    }

    private fun isCommandClose(text: String): Boolean {
        return text.matches("^/space_close(?:\\s|$)".toRegex()) || text.matches("^/space_close@$botUsername(?:\\s|$)".toRegex())
    }

    private fun isCommandOpen(text: String): Boolean {
        return text.matches("^/space_open(?:\\s|$)".toRegex()) || text.matches("^/space_open@$botUsername(?:\\s|$)".toRegex())
    }

    private fun isCommandCO2(text: String): Boolean {
        return text.matches("^/co2(?:\\s|$)".toRegex()) || text.matches("^/co2@$botUsername(?:\\s|$)".toRegex())
    }

    private fun isAdminChat(chat: Chat) = adminChats.contains(chat.id)

    override fun getBotUsername() = username

    override fun getBotToken() = token

    companion object {
        private const val generalChatId = -1001024943275L
        private val adminChats = arrayOf(
                -1001060092077L, // frontierChatId
                -1001087190182L // keyholdersChatId
        )

        private val dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")

        init {
            dateFormat.timeZone = TimeZone.getTimeZone("Asia/Taipei")
        }

        val dateTime: String
            get() = dateFormat.format(Date())

        val updates: Counter = Counter.build()
            .name("updates_received_total")
            .help("Total received updates.")
            .register()

        val updatesUnhandled: Counter = Counter.build()
            .name("updates_received_unhandled_total")
            .help("Total unhandled received updates.")
            .register()

        val commandCalls: Counter = Counter.build()
            .name("command_calls_total")
            .help("Total command calls.")
            .labelNames("cmd")
            .register()

        val callbackQueryCalls: Counter = Counter.build()
            .name("callback_query_calls_total")
            .help("Total callback query calls.")
            .register()

        val telegramApiCalls: Counter = Counter.build()
            .name("telegram_bots_api_calls_total")
            .help("Total Telegram Bots API calls.")
            .labelNames("method")
            .register()

        val spaceDoorState: Gauge = Gauge.build()
            .name("moztw_space_door_state")
            .help("The state of MozTW Space Door. 0 = closed, 1 = opened")
            .register()

        val spaceDoorStateTime: Gauge = Gauge.build()
            .name("moztw_space_door_state_time")
            .help("The update time for the state of MozTW Space Door.")
            .register()
    }
}
