package model.data.abilities

import model.data.players.PlanetsidePlayer

class MedicAbility : Ability {
    override fun useAbility(player: PlanetsidePlayer) {
        player.health = 100
    }
}