package planet.accountservice

import model.data.players.PlanetsidePlayer

class AccountService: IAccountService{
    override fun changeUsername(player: PlanetsidePlayer, username: String): Boolean {
        //check if user exists
        if(!Auraxis.removePlayer(player)) return false
        val p = player.copy(username)
        return Auraxis.addPlayer(p)
    }
}