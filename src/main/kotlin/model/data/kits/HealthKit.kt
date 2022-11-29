package model.data.kits

import model.data.players.PlanetsidePlayer

class HealthKit : Kit {
    override fun visit(planetsidePlayer: PlanetsidePlayer) {
        planetsidePlayer.health = 100
    }
}