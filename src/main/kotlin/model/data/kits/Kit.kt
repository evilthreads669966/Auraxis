package model.data.kits

import model.data.players.PlanetsidePlayer

interface Kit {
    fun visit(player: PlanetsidePlayer)
}