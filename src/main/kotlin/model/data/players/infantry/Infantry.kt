package model.data.players.infantry

import model.data.players.types.Faction
import model.data.guns.Gun
import model.data.players.PlanetsidePlayer
import model.data.players.factories.PlayerFactory

class Infantry private constructor(override var gun: Gun, override val username: String, override val faction: Faction) : PlanetsidePlayer() {
    companion object : PlayerFactory {
        override fun createPlayer(username: String, gun: Gun, faction: Faction): PlanetsidePlayer = Infantry(gun, username,faction)
    }

    override fun copy(username: String): PlanetsidePlayer {
        return createPlayer(username, this.gun, this.faction)
    }

    override fun factionChant() {
        lateinit var message: String
        when(faction){
            Faction.NEW_CONGLOMERATE -> message = "For the NC!"
            Faction.TERRAN_REPUBLIC -> message = "For the TR!"
            Faction.VANU_SOVEREIGNTY -> message = "For the VANU!"
        }
        println(message)
    }

    override fun factionDance() {
        println("Do dance")
    }
}