package model.data.abilities

import model.data.players.PlanetsidePlayer

class GrenadeAbility : Ability {
    override fun useAbility(player: PlanetsidePlayer) {
        player.health -= 50
    }
}