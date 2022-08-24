package org.moztw.bot.telegram.space

import io.prometheus.client.exporter.HTTPServer
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

    Option("p", "port", true, "the port of the prometheus exporter").run {
        this.isRequired = false
        options.addOption(this)
    }

    try {
        val cmd = DefaultParser().parse(options, args)
        val botUsername = cmd.getOptionValue("username")
        val botToken = cmd.getOptionValue("token")
        val exporterListenPort = cmd.getOptionValue("port", "")

        if (exporterListenPort.isNotEmpty()) {
            HTTPServer.Builder()
                .withPort(exporterListenPort.toInt())
                .build()
        }

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
