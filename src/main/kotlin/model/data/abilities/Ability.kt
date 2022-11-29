package model.data.abilities

import model.data.players.PlanetsidePlayer

interface Ability {
    fun useAbility(player: PlanetsidePlayer)
}
