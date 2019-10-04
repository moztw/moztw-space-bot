package org.moztw.bot.telegram.space.co2.model

data class Co2(
        val channel: Channel,
        val feeds: Array<Feed>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Co2

        if (channel != other.channel) return false
        if (!feeds.contentEquals(other.feeds)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = channel.hashCode()
        result = 31 * result + feeds.contentHashCode()
        return result
    }
}
