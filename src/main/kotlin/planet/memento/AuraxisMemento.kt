package planet.memento

import model.data.factions.ControllingFaction
import model.data.players.PlanetsidePlayer

data class AuraxisMemento(val controllingFaction: ControllingFaction, val players: MutableSet<PlanetsidePlayer>)
