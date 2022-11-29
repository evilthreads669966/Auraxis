package model.data.players

import Kit
import model.data.abilities.Ability
import model.data.abilities.GrenadeAbility
import model.data.players.types.Faction
import model.data.guns.Gun

abstract class PlanetsidePlayer {
    abstract val username: String
    abstract val faction: Faction
    abstract var gun: Gun
    private var ability: Ability = GrenadeAbility()
    open var health = 100
        set(value) {
            if (value <= 100 && value >= 0)
                field = value
        }

    override fun equals(other: Any?): Boolean {
        return other is PlanetsidePlayer && other.username == username
    }

    override fun hashCode(): Int {
        return username.hashCode()
    }

    abstract fun copy(username: String): PlanetsidePlayer

    open fun setAbility(ability: Ability) = this.apply { this.ability = ability }

    open fun useAbility(player: PlanetsidePlayer) {
        ability.useAbility(player)
    }

    open fun attack(player: PlanetsidePlayer) {
        player.health -= gun.dmg
        gun.ammo--
    }

    fun accept(visitor: Kit) {
        visitor.visit(this)
    }

    fun whisper(receiver: PlanetsidePlayer, msg: String) {
        CommunicationsCenter.sendMessage(this, receiver, msg)
    }

    fun shout(msg: String) {
        CommunicationsCenter.broadcastMessage(this, msg)
    }

    fun displayMessage(sender: PlanetsidePlayer, msg: String) {
        println("${sender.username}: $msg")
    }

    fun celebrate() {
        factionDance()
        factionChant()
    }

    abstract fun factionChant()

    abstract fun factionDance()
}