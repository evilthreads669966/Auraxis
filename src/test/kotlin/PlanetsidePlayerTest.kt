import com.google.gson.Gson
import model.data.abilities.MedicAbility
import model.data.factions.ControllingFaction
import model.data.guns.GaussRifle
import model.data.guns.ScatterCannon
import model.data.guns.Turret
import model.data.guns.addons.HighVelocityAmmo
import model.data.guns.addons.RedDotScope
import model.data.kits.AmmoKit
import model.data.kits.types.KitType
import model.data.players.infantry.Infantry
import model.data.players.infantry.suits.MaxSuit
import model.data.players.types.Faction
import org.junit.jupiter.api.Test
import planet.memento.AuraxisCaretaker
import spawnpoints.VehicleDepot
import spawnpoints.WarpGate
import java.io.BufferedReader
import java.net.Socket

class PlanetsidePlayerTest {

    @Test
    fun maxHealth100(){
        val player = WarpGate.spawn("test", GaussRifle(), Faction.NEW_CONGLOMERATE)
        player.health -= 50
        player.health += 100
        assert(player.health == 50)
    }
    @Test
    fun consumeHealthKitTest() {
        val player = WarpGate.spawn("test", GaussRifle(), Faction.NEW_CONGLOMERATE)
        player.health -= 50
        val kit = Auraxis.kitFactory.getKit(KitType.HEALTH)
        player.accept(kit)
        assert(player.health == 100)
    }

    @Test
    fun consumeAmmoKitTest(){
        val player = WarpGate.spawn("test", GaussRifle(), Faction.NEW_CONGLOMERATE)
        player.gun.ammo -= 50
        val kit = Auraxis.kitFactory.getKit(KitType.AMMO)
        player.accept(kit)
        assert(player.gun.ammo == 160)
    }

    @Test
    fun attackTest() {
        val player = WarpGate.spawn("test", GaussRifle(), Faction.NEW_CONGLOMERATE)
        val otherPlayer = WarpGate.spawn("othertest", GaussRifle(), Faction.TERRAN_REPUBLIC)
        player.attack(otherPlayer)
        assert(otherPlayer.health == 75)
    }

    @Test
    fun equalsTest(){
        val player = WarpGate.spawn("chris", GaussRifle(), Faction.NEW_CONGLOMERATE)
        val otherPlayer = WarpGate.spawn("evilthreads", GaussRifle(), Faction.NEW_CONGLOMERATE)
        assert(!player.equals(otherPlayer))
        assert(player == player)
    }
    @Test
    fun copyTest(){
        val player = WarpGate.spawn("chris", GaussRifle(), Faction.NEW_CONGLOMERATE)
        val other = player.copy("evilthreads")
        assert(other.username == "evilthreads")
    }
    @Test
    fun playersAreCorrectFaction(){
        val vanu = Infantry.createPlayer("chris", GaussRifle(), Faction.VANU_SOVEREIGNTY)
        val tr = Infantry.createPlayer("thomas", GaussRifle(), Faction.TERRAN_REPUBLIC)
        val nc = Infantry.createPlayer("evilthreads", GaussRifle(), Faction.NEW_CONGLOMERATE)
        assert(vanu.faction == Faction.VANU_SOVEREIGNTY)
        assert(tr.faction == Faction.TERRAN_REPUBLIC)
        assert(nc.faction == Faction.NEW_CONGLOMERATE)
    }
}

class MaxSuitTest{
    @Test
    fun maxSuitDamageIs10(){
        val infantry = Infantry.createPlayer("evilthreads", GaussRifle(), Faction.NEW_CONGLOMERATE)
        val max = MaxSuit(infantry, ScatterCannon())
        val vanu = Infantry.createPlayer("chris", GaussRifle(), Faction.VANU_SOVEREIGNTY)
        max.attack(vanu)
        assert(vanu.health == 90)
    }
}

class GunTest {
    @Test
    fun highVelocityIncreaseDamage() {
        val gun = GaussRifle()
        assert(gun.dmg == 25)
        val highVelocityGun = HighVelocityAmmo(gun)
        assert(highVelocityGun.dmg == 30)
    }

    @Test
    fun redDotScopeTest(){
        val rifle = RedDotScope(GaussRifle())
        assert(rifle.dmg == 26)
    }

    @Test
    fun ammoKitTest(){
        val player = WarpGate.spawn("test", GaussRifle(), Faction.NEW_CONGLOMERATE)
        player.gun.ammo -= 25
        player.accept(AmmoKit())
        assert(player.gun.ammo == 160)
    }

    @Test
    fun maxAmmo160(){
        val rifle = GaussRifle()
        rifle.ammo -= 50
        rifle.ammo += 100
        println(rifle.ammo != 160)
    }
}

class VehicleTest {
    @Test
    fun flashTest() {
        val player = WarpGate.spawn("evilthreads", GaussRifle(), Faction.NEW_CONGLOMERATE)
        val vehicle = VehicleDepot.spawnVehicle(player, RedDotScope(Turret()))
        val otherPlayer = WarpGate.spawn("fool", GaussRifle(), Faction.TERRAN_REPUBLIC)
        val otherVehicle = VehicleDepot.spawnVehicle(otherPlayer, Turret())
        vehicle.attack(otherVehicle)
        vehicle.attack(otherPlayer)
        assert(otherPlayer.health == 50)
        assert(otherVehicle.health == 50)
    }
}

class SatelliteTest{
    @Test
    fun populationSatelliteTest(){
        val player = WarpGate.spawn("chris", GaussRifle(), Faction.NEW_CONGLOMERATE)
        val client = Socket("localhost", 6666)
        val reader = BufferedReader(client.getInputStream().reader())
        val result = reader.read()
        reader.close()
        client.close()
        assert(result == 1)
    }

    @Test
    fun playerSatelliteTest(){
        val player = WarpGate.spawn("chris", GaussRifle(), Faction.NEW_CONGLOMERATE)
        val socket = Socket("localhost", 7777)
        val reader = BufferedReader(socket.getInputStream().reader())
        val players = reader.readText()
        reader.close()
        socket.close()
        val playerJson = Gson().toJson(Auraxis.toList())
        assert(players == playerJson)
    }
}

class KitFactoryTest{
    @Test
    fun ammoKitTest(){
        val kit = Auraxis.kitFactory.getKit(KitType.AMMO)
        val otherKit = Auraxis.kitFactory.getKit(KitType.AMMO)
        assert(kit === otherKit)
    }

    @Test
    fun healthKitTest(){
        val kit = Auraxis.kitFactory.getKit(KitType.HEALTH)
        val otherKit = Auraxis.kitFactory.getKit(KitType.HEALTH)
        assert(kit === otherKit)
    }
}

class AuraxisTest {
    @Test
    fun iteratorTest() {
        val player = WarpGate.spawn("chris", GaussRifle(), Faction.NEW_CONGLOMERATE)
        assert(Auraxis.count() == 1)
    }

    @Test
    fun addRemovePlayerTest(){
        val player = WarpGate.spawn("chris", GaussRifle(), Faction.NEW_CONGLOMERATE)
        assert(Auraxis.population == 1)
        Auraxis.removePlayer(player)
        assert(Auraxis.population == 0)
    }
    @Test
    fun populationTest(){
        val player = WarpGate.spawn("chris", GaussRifle(), Faction.NEW_CONGLOMERATE)
        assert(Auraxis.population == 1)
        Auraxis.removePlayer(player)
        assert(Auraxis.population == 0)
    }

    @Test
    fun changeUsername(){
        val player = WarpGate.spawn("chris", GaussRifle(), Faction.NEW_CONGLOMERATE)
        Auraxis.accountService.changeUsername(player, "evilthreads")
        assert(Auraxis.first().username == "evilthreads")
        assert(Auraxis.population == 1)
    }

    @Test
    fun changeUsernameToExistingUsername(){
        val player = WarpGate.spawn("chris", GaussRifle(), Faction.NEW_CONGLOMERATE)
        val other = WarpGate.spawn("evilthreads", GaussRifle(), Faction.NEW_CONGLOMERATE)
        val changed = Auraxis.accountService.changeUsername(player, "evilthreads")
        assert(!changed)
    }

    @Test
    fun findUserByUsername(){
        val player = WarpGate.spawn("chris", GaussRifle(), Faction.NEW_CONGLOMERATE)
        val other = Auraxis.findPlayerByUsername("chris")
        assert(player == other)
    }

    @Test
    fun controllingFactionTest(){
        assert(Auraxis.controllingFaction == ControllingFaction.Contested)
    }

    @Test
    fun countForFactionTest(){
        WarpGate.spawn("chris", GaussRifle(), Faction.NEW_CONGLOMERATE)
        WarpGate.spawn("evilthreads", GaussRifle(), Faction.TERRAN_REPUBLIC)
        WarpGate.spawn("alien", GaussRifle(), Faction.VANU_SOVEREIGNTY)
        assert(Auraxis.countForFaction(Faction.NEW_CONGLOMERATE) == 1)
        assert(Auraxis.countForFaction(Faction.TERRAN_REPUBLIC) == 1)
        assert(Auraxis.countForFaction(Faction.VANU_SOVEREIGNTY) == 1)
    }
}



class AccountServiceTest{

}

class AuraxisCaretakerTest{
    @Test
    fun saveAndRestoreTest(){
        val player = WarpGate.spawn("chris", GaussRifle(), Faction.NEW_CONGLOMERATE)
        val memento = Auraxis.createMemento()
        AuraxisCaretaker.save(memento)
        val other = WarpGate.spawn("evilthreads", GaussRifle(), Faction.NEW_CONGLOMERATE)
        Auraxis.restore(AuraxisCaretaker.restore(0))
        assert(Auraxis.population == 1)
        assert(Auraxis.controllingFaction == ControllingFaction.Contested)
    }
}

class AbilitiesTest{
    @Test
    fun grenadierAbilityTest(){
        val player = WarpGate.spawn("chris", GaussRifle(), Faction.NEW_CONGLOMERATE)
        val other = WarpGate.spawn("thomas", GaussRifle(), Faction.TERRAN_REPUBLIC)
        player.useAbility(other)
        assert(other.health == 50)
    }

    @Test
    fun medicAbilityTest(){
        val soldier = WarpGate.spawn("chris", GaussRifle(), Faction.NEW_CONGLOMERATE)
        val enemy = WarpGate.spawn("thomas", GaussRifle(), Faction.TERRAN_REPUBLIC)
        val enemyMedic = WarpGate.spawn("evilthreads", GaussRifle(), Faction.TERRAN_REPUBLIC)
        soldier.useAbility(enemy)
        enemyMedic.setAbility(MedicAbility())
        enemyMedic.useAbility(enemy)
        assert(enemy.health == 100)
    }
}