package model.satellites

import com.google.gson.Gson
import java.io.BufferedWriter

class PlayersSatellite: ISatellite {
    override fun broadcast() = multiThreadedServer(7777){ sock ->
        val writer = BufferedWriter(sock.getOutputStream().writer())
        val jsonResponse = Gson().toJson(planet.toList())
        writer.write(jsonResponse)
        writer.close()
    }
}