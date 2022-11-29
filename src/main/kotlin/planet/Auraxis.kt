/*
Copyright 2022 Chris Basinger

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/
import model.data.factions.ControllingFaction
import model.data.kits.factory.KitFactory
import model.data.players.PlanetsidePlayer
import model.data.players.types.Faction
import model.data.satellites.PlayersSatellite
import model.data.satellites.PopulationSatellite
import planet.IPlanet
import planet.accountservice.AccountService
import planet.accountservice.IAccountService
import planet.logging.AuraxisLogger
import planet.logging.AuraxisObserver
import planet.memento.AuraxisMemento

/**
 * @author Chris Basinger
 * @email evilthreads669966@gmail.com
 * @date 11/17/22
 * Auraxis is written using only software design patterns. It doesn't actually do anything useful.
 **/

object Auraxis : IPlanet, Iterable<PlanetsidePlayer> {
    private var players = mutableSetOf<PlanetsidePlayer>()
    private val observers = mutableListOf<AuraxisObserver>(AuraxisLogger())
    override var controllingFaction: ControllingFaction = ControllingFaction.Contested
    override val population: Int
        get() = players.size
    override val satellites = listOf(PopulationSatellite(), PlayersSatellite())
    override val accountService: IAccountService = AccountService()
    override val kitFactory: KitFactory = KitFactory()

    init {
        notifyObservers("Starting Auraxis")
        broadcast()
        notifyObservers("Satellites broadcasting")
    }

    fun findPlayerByUsername(username: String): PlanetsidePlayer? = players.firstOrNull{ it.username == username }

    fun addObserver(observer: AuraxisObserver) {
        observers.add(observer)
    }

    fun notifyObservers(msg: String) = observers.forEach { it.update(msg) }

    fun createMemento(): AuraxisMemento {
        notifyObservers("auraxis backup created")
        return AuraxisMemento(controllingFaction, players)
    }

    fun restore(memento: AuraxisMemento) {
        players = memento.players
        controllingFaction = memento.controllingFaction
        notifyObservers("auraxis restored from backup")
    }

    override fun addPlayer(player: PlanetsidePlayer): Boolean {
        if(players.add(player)){
            notifyObservers("${player.username} joined")
            return true
        }
        return false
    }

    override fun removePlayer(player: PlanetsidePlayer): Boolean {
        if(players.remove(player)){
            notifyObservers("${player.username} quit")
            return true
        }
        return false
    }

    fun playersForFaction(faction: Faction): List<PlanetsidePlayer> = players.filter { it.faction == faction }

    override fun countForFaction(faction: Faction): Int = players.count { it.faction == faction }

    override fun iterator(): Iterator<PlanetsidePlayer> = players.iterator()
}