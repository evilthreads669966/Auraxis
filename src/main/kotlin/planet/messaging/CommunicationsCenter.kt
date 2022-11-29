package planet.messaging

import model.data.players.PlanetsidePlayer

object CommunicationsCenter {
    fun broadcastMessage(sender: PlanetsidePlayer, msg: String) {
        Auraxis.filter { it == sender }.forEach { player -> player.displayMessage(sender, msg) }
    }

    fun sendMessage(sender: PlanetsidePlayer, receiver: PlanetsidePlayer, msg: String) {
        receiver.displayMessage(sender, msg)
    }
}
