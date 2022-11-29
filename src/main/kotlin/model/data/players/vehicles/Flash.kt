package model.data.players.vehicles

import model.data.players.types.Faction
import model.data.guns.Gun
import model.data.players.PlanetsidePlayer
import model.data.players.factories.VehicleFactory

class Flash private constructor(val player: PlanetsidePlayer, gun: Gun) : PlanetsidePlayer() {
    override val username: String = player.username
    override val faction: Faction = player.faction
    override var gun: Gun = gun


    companion object : VehicleFactory {
        override fun createVehicle(player: PlanetsidePlayer, gun: Gun): PlanetsidePlayer {
            return Flash(player, gun)
        }
    }

    override fun copy(username: String): PlanetsidePlayer {
        return createVehicle(player.copy(username), this.gun)
    }

    fun honk() {
        println("HONK!")
    }

    override fun factionChant() {
        honk()
        player.factionChant()
    }

    override fun factionDance() = honk()
}

