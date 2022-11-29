package planet

import IAccountService
import model.data.factions.ControllingFaction
import model.data.kits.factory.KitFactory
import model.data.players.PlanetsidePlayer
import model.data.players.types.Faction
import model.data.satellites.ISatellite

interface IPlanet: ISatellite {
    val controllingFaction: ControllingFaction
    val isContested: Boolean
        get() = controllingFaction == ControllingFaction.Contested
    val population: Int
    val satellites: List<ISatellite>
    val accountService: IAccountService
    val kitFactory: KitFactory

    override fun broadcast() {
        satellites.forEach { it.broadcast() }
    }

    fun addPlayer(player: PlanetsidePlayer): Boolean

    fun removePlayer(player: PlanetsidePlayer): Boolean

    fun countForFaction(faction: Faction): Int
}
