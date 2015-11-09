package de.vs.monopoly

import scala.collection.mutable.ListBuffer

object Games { //GamesFacade

  //variables
  var _id = 0
  var games: Map[String, Game] = Map()

  //methods
  def id(): String = { _id += 1; _id.toString }

  def createNewGame(): Game = {

    //Init Components //Generate Components fehlt
    val _game = ""
    val dice = ""
    val board = ""
    val bank = ""
    val broker = ""
    val decks = ""
    val events = ""
    val _components = Components(_game, dice, board, bank, broker, decks, events)

    var game = Game(components = _components)
    games += (game.gameid -> game)
    game
  }

  def getGame(gameid: String): Option[Game] = {
    games.get(gameid)
  }

  def getPlayer(gameid: String, playerid: String): Option[Player] = {
    getGame(gameid) match {
      case Some(game) => game.players.filter { x => x.id == playerid }.headOption
      case None => None
    }
  }

  def removePlayer(gameid: String, playerid: String) {
    getGame(gameid) match {
      case Some(game) => game.players = game.players.filterNot { x => x.id == playerid }
      case None =>
    }
  }

  //woher weiss der Spieler das er gejoint ist? Maximum festlegen?
  def joinGame(gameid: String, name: String) {
    var player = Player(id = Players.id(), name = name, uri = "http://localhost:4567/player/" + name)
    getGame(gameid) match {
      case Some(game) => game.players +:= player
      case None =>
    }
  }

  def isPlayerReady(gameid: String, playerid: String): Boolean = {
    getPlayer(gameid, playerid) match {
      case Some(player) => player.ready
      case None => false
    }
  }

  def setPlayerReady(gameid: String, playerid: String) {
    getPlayer(gameid, playerid) match {
      case Some(player) => player.ready = true;
      case None =>
    }
  }

  def getCurrentPlayer(gameid: String): Option[Player] = {
    getGame(gameid) match {
      case Some(game) => game.players.headOption
      case None => None
    }
  }

  def getMutex(gameid: String): Option[String] = {
    getGame(gameid) match {
      case Some(game) => Some(game.mutex)
      case None => None
    }
  }

  def setMutex(gameid: String, playerid: String): String = {
    getGame(gameid) match {
      case Some(game) =>
        if (playerid == game.mutex) {
          "200"
        } else if (playerid == "") {
          game.mutex = playerid
          "201"
        } else {
          "409"
        }
      case None => ""
    }
  }

  def resetMutex(gameid: String) {
    getGame(gameid) match {
      case Some(game) => game.mutex = ""        
      case None => //Gibt es nicht?
    }
  }

  def apply() = new Games(games) //getGames
}

case class Components(game: String, dice: String, board: String, bank: String, broker: String, decks: String, events: String)
case class Games(games: Map[String, Game])

//get /boards wieso enthält ein Game kein ready und players?
case class Game(gameid: String = Games.id().toString, var players: List[Player] = List(), components:Components, started: Boolean = false, var mutex: String = "") {
  //override def toString() = "{ \"gameid\":" + "\"" + gameid + "\"" + "}" //Muell
}