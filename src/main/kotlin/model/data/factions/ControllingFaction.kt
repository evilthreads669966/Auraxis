package model.data.factions

sealed class ControllingFaction {
    object Vanu : ControllingFaction()
    object TerranRepublic : ControllingFaction()
    object NewConglomerate : ControllingFaction()
    object Contested : ControllingFaction()
}
