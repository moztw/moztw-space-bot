package org.moztw.bot.telegram.space.co2.model

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.ws.rs.core.UriBuilder

data class Chart(
        val data: List<Pair<ZonedDateTime, String>>
) {
    private val dataX = data.map { it.first.format(DateTimeFormatter.ofPattern("HH:mm")) }
    private val dataY = data.map { it.second.toInt() }

    private val dataMin = dataY.minBy { it } ?: 0
    private val dataMax = dataY.maxBy { it } ?: 0
    private val dataMid = (dataMin + dataMax) / 2
    private val dataQuad = arrayOf(dataMin, (dataMin + dataMid) / 2, dataMid, (dataMid + dataMax) / 2, dataMax)

    private val chartData = dataY.joinToString(",", "t:")
    private val chartDataScale = "$dataMin,$dataMax"
    private val chartGrid = "10,25"
    private val chartLabel = dataX.filterIndexed { index, _ -> 99 == index || 0 == (index % 10) }.joinToString("|")
    private val chartSize = "640x360"
    private val chartType = "lc"
    private val chartTitle = "Concentration of CO2 in Mozilla Community Space Taipei"
    private val chartAxisLabel = "1:|${dataQuad.joinToString("|") { "$it ppm" }}"
    private val chartAxis = "x,y"

    val url = UriBuilder
            .fromPath("https://chart.googleapis.com/chart")
            .queryParam("chd", chartData)
            .queryParam("chds", chartDataScale)
            .queryParam("chg", chartGrid)
            .queryParam("chl", chartLabel)
            .queryParam("chs", chartSize)
            .queryParam("cht", chartType)
            .queryParam("chtt", chartTitle)
            .queryParam("chxl", chartAxisLabel)
            .queryParam("chxt", chartAxis)
            .build()
            .toASCIIString()!!
}
