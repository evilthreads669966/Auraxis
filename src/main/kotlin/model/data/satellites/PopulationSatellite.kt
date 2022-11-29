package model.data.satellites

import java.io.BufferedWriter

class PopulationSatellite: ISatellite{
    override fun broadcast() = multiThreadedServer(6666){ sock ->
        val writer = BufferedWriter(sock.getOutputStream().writer())
        writer.write(Auraxis.population)
        writer.close()
    }
}
