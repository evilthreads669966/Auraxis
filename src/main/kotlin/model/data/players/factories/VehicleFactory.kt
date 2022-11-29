package model.data.players.factories

import model.data.guns.Gun
import model.data.players.PlanetsidePlayer

interface VehicleFactory {
    fun createVehicle(player: PlanetsidePlayer, gun: Gun): PlanetsidePlayer
}