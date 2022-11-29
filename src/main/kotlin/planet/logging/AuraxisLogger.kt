package planet.logging

import java.io.File
import java.time.Instant
import java.time.format.DateTimeFormatter

class AuraxisLogger : AuraxisObserver {
    private val log = File("log.txt")

    init {
        if (!log.exists())
            log.createNewFile()
    }

    private fun log(msg: String) {
        val timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
        log.appendText("\n$timestamp - $msg")
    }

    override fun update(msg: String) = log(msg)
}
