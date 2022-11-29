package planet.accountservice

import model.data.players.PlanetsidePlayer

interface IAccountService{
    fun changeUsername(player: PlanetsidePlayer, String: String): Boolean
}