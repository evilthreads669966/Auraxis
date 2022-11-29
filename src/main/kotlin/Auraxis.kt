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
import java.io.BufferedWriter
import java.io.File
import java.net.ServerSocket
import java.net.Socket
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executors

/*
            (   (                ) (             (     (
            )\ ))\ )    *   ) ( /( )\ )     (    )\ )  )\ )
 (   (   ( (()/(()/(  ` )  /( )\()|()/((    )\  (()/( (()/(
 )\  )\  )\ /(_))(_))  ( )(_)|(_)\ /(_))\((((_)( /(_)) /(_))
((_)((_)((_|_))(_))   (_(_()) _((_|_))((_))\ _ )(_))_ (_))
| __\ \ / /|_ _| |    |_   _|| || | _ \ __(_)_\(_)   \/ __|
| _| \ V /  | || |__    | |  | __ |   / _| / _ \ | |) \__ \
|___| \_/  |___|____|   |_|  |_||_|_|_\___/_/ \_\|___/|___/
....................../´¯/)
....................,/¯../
.................../..../
............./´¯/'...'/´¯¯`·¸
........../'/.../..../......./¨¯\
........('(...´...´.... ¯~/'...')
.........\.................'...../
..........''...\.......... _.·´
............\..............(
..............\.............\...
*/
/**
 * @author Chris Basinger
 * @email evilthreads669966@gmail.com
 * @date 11/17/22
 * Auraxis is written using only software design patterns. It doesn't actually do anything useful.
 **/
enum class Faction {
    NEW_CONGLOMERATE, VANU_SOVEREIGNTY, TERRAN_REPUBLIC,
}

sealed class ControllingFaction {
    object Vanu : ControllingFaction()
    object TerranRepublic : ControllingFaction()
    object NewConglomerate : ControllingFaction()
    object Contested : ControllingFaction()
}

/*Base gun*/
interface Gun {
    var dmg: Int
    var ammo: Int
}

/*Gun addon that adds plus 5 to the damage*/
class HighVelocityAmmo(gun: Gun) : Gun by gun {
    override var dmg: Int = gun.dmg + 5
}

/*A gun addon that adds plus 1 to the damage*/
class RedDotScope(gun: Gun) : Gun by gun {
    override var dmg: Int = gun.dmg + 1
}

interface Ability {
    fun useAbility(player: PlanetsidePlayer)
}

class GrenadeAbility : Ability {
    override fun useAbility(player: PlanetsidePlayer) {
        player.health -= 50
    }
}

class MedicAbility : Ability {
    override fun useAbility(player: PlanetsidePlayer) {
        player.health = 100
    }
}

class GaussRifle : Gun {
    override var dmg = 25
    override var ammo = 160
        set(value) {
            if (value <= 160 && value >= 0)
                field = value
        }
}

interface PlayerFactory {
    fun createPlayer(username: String, gun: Gun, faction: Faction): PlanetsidePlayer
}

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

interface Kit {
    fun visit(player: PlanetsidePlayer)
}

class HealthKit : Kit {
    override fun visit(planetsidePlayer: PlanetsidePlayer) {
        planetsidePlayer.health = 100
    }
}

class AmmoKit : Kit {
    override fun visit(player: PlanetsidePlayer) {
        player.gun.ammo = 160
    }
}

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

    fun playersForFaction(faction:Faction): List<PlanetsidePlayer> = players.filter { it.faction == faction }

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


class Turret : Gun {
    override var dmg: Int = 50
    override var ammo: Int = 200
}

interface VehicleFactory {
    fun createVehicle(player: PlanetsidePlayer, gun: Gun): PlanetsidePlayer
}

enum class VehicleType {
    FLASH
}

object VehicleSpawnPoint {
    fun spawn(player: PlanetsidePlayer, gun: Gun): PlanetsidePlayer {
        return Flash.createVehicle(player, gun)
    }
}

class Flash private constructor(val player: PlanetsidePlayer, gun: Gun) : PlanetsidePlayer() {
    override val username: String = player.username
    override val faction: Faction = player.faction
    override var gun: Gun = gun


    companion object : VehicleFactory {
        override fun createVehicle(player: PlanetsidePlayer, gun: Gun): PlanetsidePlayer {
            return Flash(player, gun)
        }
    }

    override fun copy(username: String): PlanetsidePlayer {
        return createVehicle(player.copy(username), this.gun)
    }

    fun honk() {
        println("HONK!")
    }

    override fun factionChant() {
        honk()
        player.factionChant()
    }

    override fun factionDance() = honk()
}

enum class KitType{
    AMMO, HEALTH,
}

class KitFactory{
    private val kits = hashMapOf<KitType,Kit>()

    fun getKit(type: KitType): Kit{
        if(kits[type] != null)
            return kits[type]!!
        return when(type){
            KitType.HEALTH -> {
                val kit = HealthKit()
                kits[type] = kit
                kit
            }
            KitType.AMMO ->{
                val kit = AmmoKit()
                kits[type] = kit
                kit
            }
        }
    }
}