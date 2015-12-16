package de.vs.monopoly.logic

import play.api.Logger


object Games {
  //GamesFacade
  //variables
  var _id = 0
  var games: Map[String, Game] = Map()

  //methods
  def id(): String = {
    _id += 1
    _id.toString
  }

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
    Logger.info("created game " + game.gameid)
    game
  }

  //dirty
  def findPlayer(playerid: String): Option[Player] = {
    var rs: Option[Player] = None
    for (game <- games.values) {
      game.players.find(p => p.id == playerid) match {
        case Some(player) => rs = Some(player)
        case None =>
      }
    }
    rs
  }

  def getGame(gameid: String): Option[Game] = {
    games.get(gameid)
  }

  def getPlayer(gameid: String, playerid: String): Option[Player] = {
    getGame(gameid) match {
      case Some(game) =>
        Logger.info("game " + gameid + " found.")
        game.players.find(x => x.id == playerid)
      case None =>
        Logger.info("game" + gameid + " missing.")
        None
    }
  }

  //@TODO event to board remove player?
  def removePlayer(gameid: String, playerid: String) {
    getGame(gameid) match {
      case Some(game) =>
        val old = game.players
        game.players = game.players.filterNot { x => x.id == playerid }
        Logger.info("player " + playerid + " is not in the game anymore.")
        //remove game if has no players
        //@TODO check has changed needed??
        if (game.players.size < old.size && game.players.isEmpty) {
          removeGame(gameid)
          Logger.info("game empty and removed " + gameid)
        }
      case None =>
        Logger.info("Error removePlayer " + playerid + " in game" + gameid + " game not found.")

    }
  }

  def removeGame(gameid: String): Unit = {
    games -= gameid
  }

  //@TODO ?
  //woher weiss der Spieler das er gejoint ist? Maximum festlegen?
  def joinGame(gameid: String, _id: String, _name: String, _uri: String): Option[Player] = {
    var player = Player(id = _id, name = _name, gameid = gameid, uri = _uri)
    getGame(gameid) match {
      case Some(game) =>
        if (!game.started) {
          Logger.info("player " + player.id + " joined Game " + gameid)
          game.players :+= player
          Some(player)
        } else {
          Logger.info("Error " + gameid + " cant join game, already started")
          None
        }
      case None => Logger.info("Error " + gameid + " joinGame"); None
    }
  }

  def isPlayerReady(gameid: String, playerid: String): Boolean = {
    getPlayer(gameid, playerid) match {
      case Some(player) =>
        Logger.info("player " + playerid + " is ready")
        player.ready
      case None => false
    }
  }

  //Spielerwechsel? und lobby ready
  def setPlayerReady(gameid: String, playerid: String) {

    getPlayer(gameid, playerid) match {
      case Some(playerTry) =>
        //Game started
        if (getGame(gameid).get.started) {
          getCurrentPlayer(gameid) match {
            case Some(player) =>
              if (player.id == playerid) {
                //check ob spieler gerade dran auch der jenige, der ready drueckt
                getGame(gameid) match {
                  case Some(game) =>
                    game.players = player :: game.players.tail // filterNot( x => x.id == player.id)
                    playerTry.ready = true
                    Logger.info("setPlayerReady " + playerid + " is ready now")
                    resetMutex(gameid)
                    setMutex(gameid, player.id)
                  case None => None
                }
              }
            case None => None
          }
        } else {
          //Game not started lobby
          Logger.info("setPlayerReady " + playerid + " lobby success")
          playerTry.ready = true
        }
      case None => Logger.info("Error " + playerid + " setPlayerReady")
    }
  }

  def startGame(gameid: String): Option[Player] = {
    //check if all players ready => start game
    val players = getPlayers(gameid)
    Logger.info("try start " + gameid + " game")
    //    players.foreach(p => println(p.ready))
    if (players.forall(_.ready == true)) {
      Logger.info("setPlayerReady game started")
      //@TODO Schon fertig?  - hier fehlt was, direktes starten des spiels nicht erlaubt, da ready true erwartet wird
      players.head.ready = false
      setMutex(gameid, players.head.id)
      getGame(gameid).get.started = true
      Logger.info("game " + gameid + " erfolgreich gestartet")
      Some(players.head)
    } else {
      Logger.info("One Player or more players are not ready " + gameid)
      None
    }
  }

  def getPlayers(gameid: String): List[Player] = {
    games.get(gameid) match {
      case Some(game) => game.players
      case None => Nil
    }
  }

  //@TODO ?
  //Die reihenfolge muss implementiert werden letzter in der Liste wird an
  //stelle eins gesetzt, wenn er dran war.
  def getCurrentPlayer(gameid: String): Option[Player] = {
    getGame(gameid) match {
      case Some(game) => game.players.headOption
      case None => None
    }
  }

  def getMutex(gameid: String): Option[String] = {
    getGame(gameid) match {
      case Some(game) =>
        Logger.info("getMutex game " + gameid)
        if (game.mutex != "") {
          Logger.info("getMutex game " + gameid + " mutex ist belegt " + game.mutex)
          Some(game.mutex)
        } else {
          None
        }
      case None => None
    }
  }

  //@TODO Zu aquiere umbennen?
  def setMutex(gameid: String, playerid: String): String = {
    //@TODO Hier einen echten mutex einbauen, da jetty async?
    Logger.info(playerid + " Try to set Mutex")
    getGame(gameid) match {
      case Some(game) =>
        if (playerid == game.mutex) {
          Logger.info(playerid + " hat den Mutex bereits")
          "200"
        } else if (game.mutex == "") {
          Logger.info(playerid + " hat den Mutex bekommen")
          game.mutex = playerid
          "201"
        } else {
          Logger.info("jemand anderes hat den Mutex")
          "409"
        }
      case None =>
        Logger.info("mutex setzen, game nicht gefunden")
        ""
    }
  }

  def resetMutex(gameid: String) {
    getGame(gameid) match {
      case Some(game) => game.mutex = ""
      case None => Logger.info("Error game " + gameid + " resetMutex")
    }
  }

  def reset(): Unit = {
    games = Map()
    _id = 0
    Players._id = 0
  }

  def getGames(): List[Game] = {
    games.values.toList
  }

  def apply() = new Games(getGames()) //getGames
}


case class Components(var game: String, var dice: String, var board: String, var bank: String, var broker: String, var decks: String, var events: String)

case class Games(games: List[Game])

//@TODO
//get /boards wieso enthält ein Game kein ready und players?
case class Game(gameid: String = Games.id().toString, var players: List[Player] = List(), components: Components, var started: Boolean = false, var mutex: String = "")