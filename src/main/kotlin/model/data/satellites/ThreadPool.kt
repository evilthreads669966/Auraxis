package model.data.satellites

import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Executors

val pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())

fun multiThreadedServer(port: Int, client: (Socket) -> Unit){
    pool.execute {
        val server = ServerSocket(port)
        while (true) {
            val socket = server.accept()
            pool.execute { client(socket) }
        }
    }
}