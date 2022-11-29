package model.data.players.factories

import model.data.guns.Gun
import model.data.players.PlanetsidePlayer
import model.data.players.types.Faction

interface PlayerFactory {
    fun createPlayer(username: String, gun: Gun, faction: Faction): PlanetsidePlayer
}