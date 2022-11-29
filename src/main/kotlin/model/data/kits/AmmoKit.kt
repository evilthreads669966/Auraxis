package model.data.kits

import model.data.players.PlanetsidePlayer

class AmmoKit : Kit {
    override fun visit(player: PlanetsidePlayer) {
        player.gun.ammo = 160
    }
}