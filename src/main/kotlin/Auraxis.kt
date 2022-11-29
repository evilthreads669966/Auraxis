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
import com.google.gson.Gson
import model.data.factions.ControllingFaction
import model.data.players.types.Faction
import model.data.guns.Gun
import model.data.kits.factory.KitFactory
import model.data.players.PlanetsidePlayer
import model.data.players.infantry.Infantry
import model.data.players.vehicles.Flash
import java.io.BufferedWriter
import java.io.File
import java.net.ServerSocket
import java.net.Socket
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executors

/**
 * @author Chris Basinger
 * @email evilthreads669966@gmail.com
 * @date 11/17/22
 * Auraxis is written using only software design patterns. It doesn't actually do anything useful.
 **/

interface SpawnPoint {
    fun spawn(username: String, gun: Gun, faction: Faction): PlanetsidePlayer
}

object WarpGate : SpawnPoint {
    override fun spawn(username: String, gun: Gun, faction: Faction): PlanetsidePlayer {
        val infantry = Infantry.createPlayer(username,gun,faction)
        Auraxis.addPlayer(infantry)
        return infantry
    }
}

object AuraxisCaretaker {
    private val mementos = mutableListOf<AuraxisMemento>()

    fun restore(index: Int): AuraxisMemento = mementos[index]
    fun save(memento: AuraxisMemento) {
        mementos.add(memento)
    }
}

interface AuraxisObserver {
    fun update(msg: String)
}

class AuraxisLogger : AuraxisObserver {
    private val log = File("log.txt")

    init {
        if (!log.exists())
            log.createNewFile()
    }

    private fun log(msg: String) {
        val timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
        log.appendText("\n$timestamp - $msg")
    }

    override fun update(msg: String) = log(msg)
}



data class AuraxisMemento(val controllingFaction: ControllingFaction, val players: MutableSet<PlanetsidePlayer>)

interface IAuraxis: ISatellite {
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

val pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())

fun multiThreadedServer(port: Int, client: (Socket) -> Unit){
    pool.execute {
        val server = ServerSocket(port)
        while (true) {
            val socket = server.accept()
            pool.execute { client(socket) }
        }
    }
}

interface ISatellite{
    fun broadcast()
}

class PopulationSatellite: ISatellite{
    override fun broadcast() = multiThreadedServer(6666){ sock ->
        val writer = BufferedWriter(sock.getOutputStream().writer())
        writer.write(Auraxis.population)
        writer.close()
    }
}

class PlayersSatellite: ISatellite {
    override fun broadcast() = multiThreadedServer(7777){ sock ->
        val writer = BufferedWriter(sock.getOutputStream().writer())
        val jsonResponse = Gson().toJson(Auraxis.toList())
        writer.write(jsonResponse)
        writer.close()
    }
}

interface IAccountService{
    fun changeUsername(player: PlanetsidePlayer, String: String): Boolean
}
class AccountService: IAccountService{
    override fun changeUsername(player: PlanetsidePlayer, username: String): Boolean {
        //check if user exists
        if(!Auraxis.removePlayer(player)) return false
        val p = player.copy(username)
        return Auraxis.addPlayer(p)
    }
}

object Auraxis : IAuraxis, Iterable<PlanetsidePlayer> {
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

//todo consider putting communication center inside of Auraxis.
object CommunicationsCenter {
    fun broadcastMessage(sender: PlanetsidePlayer, msg: String) {
        Auraxis.filter { it == sender }.forEach { player -> player.displayMessage(sender, msg) }
    }

    fun sendMessage(sender: PlanetsidePlayer, receiver: PlanetsidePlayer, msg: String) {
        receiver.displayMessage(sender, msg)
    }
}

object VehicleSpawnPoint {
    fun spawn(player: PlanetsidePlayer, gun: Gun): PlanetsidePlayer {
        return Flash.createVehicle(player, gun)
    }
}