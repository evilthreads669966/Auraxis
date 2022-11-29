package spawnpoints

import model.data.guns.Gun
import model.data.players.PlanetsidePlayer
import model.data.players.types.Faction

interface IWarpGate {
    fun spawn(username: String, gun: Gun, faction: Faction): PlanetsidePlayer
}