package org.moztw.bot.telegram.space

import org.apache.commons.cli.*
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val options = Options()

    Option("u", "username", true, "the username of the bot").run {
        this.isRequired = true
        options.addOption(this)
    }

    Option("t", "token", true, "the token of the bot").run {
        this.isRequired = true
        options.addOption(this)
    }

    try {
        val cmd = DefaultParser().parse(options, args)
        val botUsername = cmd.getOptionValue("username")
        val botToken = cmd.getOptionValue("token")

        try {
            TelegramBotsApi(DefaultBotSession::class.java).registerBot(Bot(username = botUsername, token = botToken))
        } catch (e: TelegramApiRequestException) {
            e.printStackTrace()
        }
    } catch (e: ParseException) {
        println(e.message)
        HelpFormatter().printHelp("moztw-space-bot", options)
        exitProcess(1)
    }
}
