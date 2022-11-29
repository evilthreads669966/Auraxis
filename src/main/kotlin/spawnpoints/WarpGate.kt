package spawnpoints

import model.data.guns.Gun
import model.data.players.PlanetsidePlayer
import model.data.players.infantry.Infantry
import model.data.players.types.Faction

object WarpGate : IWarpGate {
    override fun spawn(username: String, gun: Gun, faction: Faction): PlanetsidePlayer {
        val infantry = Infantry.createPlayer(username,gun,faction)
        planet.addPlayer(infantry)
        return infantry
    }
}