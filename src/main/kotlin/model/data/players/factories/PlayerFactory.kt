package model.data.players.factories

import Faction
import model.data.guns.Gun
import model.data.players.PlanetsidePlayer

interface PlayerFactory {
    fun createPlayer(username: String, gun: Gun, faction: Faction): PlanetsidePlayer
}