package spawnpoints

import model.data.guns.Gun
import model.data.players.PlanetsidePlayer
import model.data.players.vehicles.Flash

object VehicleDepot {
    fun spawnVehicle(player: PlanetsidePlayer, gun: Gun): PlanetsidePlayer {
        return Flash.createVehicle(player, gun)
    }
}