package de.vs.monopoly

//import scala.collection.mutable.ListBuffer

object Games { //GamesFacade

  //variables
  var _id = 0
  var games: Map[String, Game] = Map()

  //methods
  def id(): Int = { _id += 1; _id }

  def createNewGame(): Game = {
    var game = Game()
    games += (game.gameid -> game)
    game
  }

  def getGame(gameid: String):Option[Game] = {
    games.get(gameid)
  }

  def apply() = new Games(games) //getGames
}

case class Games(games: Map[String, Game])
case class Game(gameid: String = Games.id.toString, players: List[Player] = List(), started: Boolean = false) {

}