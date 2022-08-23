package org.moztw.bot.telegram.space.co2

import com.beust.klaxon.Klaxon
import io.prometheus.client.Counter
import okhttp3.OkHttpClient
import okhttp3.Request
import org.moztw.bot.telegram.space.co2.model.Chart
import org.moztw.bot.telegram.space.co2.model.Co2
import java.io.IOException
import java.time.ZoneId
import java.time.ZonedDateTime

class SpaceCo2 {
    private val url = "https://thingspeak.com/channels/631210/feed.json"

    @Throws(IOException::class)
    fun fetchCo2() = OkHttpClient()
            .newCall(Request.Builder().url(url).build())
            .execute().body?.string()?.let { json -> Klaxon().parse<Co2>(json) }
            .also { requests.inc() }

    fun chartPng(co2: Co2) = Chart(co2.feeds.map {
        Pair<ZonedDateTime, String>(ZonedDateTime.parse(it.created_at).withZoneSameInstant(ZoneId.of("Asia/Taipei")), it.field1)
    }.toList()).url

    companion object {
        val requests: Counter = Counter.build()
            .name("requests_co2_total")
            .help("Total CO2 query requests.")
            .register()
    }
}
