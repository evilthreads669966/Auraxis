package model.data.players.infantry.suits

import model.data.guns.Gun
import model.data.players.PlanetsidePlayer
import model.data.players.types.Faction

class MaxSuit(val player: PlanetsidePlayer, gun: Gun): PlanetsidePlayer() {
    override val username: String = player.username
    override val faction: Faction = player.faction
    override var gun: Gun = gun

    override fun copy(username: String): PlanetsidePlayer {
        return MaxSuit(player.copy(username), this.gun)
    }

    override fun factionChant() {
        player.factionChant()
    }

    override fun factionDance() {
        player.factionDance()
    }
}