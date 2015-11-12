package de.vs.monopoly

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
    println("created game")
    game
  }

  def getGame(gameid: String): Option[Game] = {
    games.get(gameid)
  }

  def getPlayer(gameid: String, playerid: String): Option[Player] = {
    getGame(gameid) match {
      case Some(game) =>
        println("get player")
        game.players.filter { x => x.id == playerid }.headOption
      case None =>
        println("get player fail")
        None
    }
  }

  def removePlayer(gameid: String, playerid: String) {
    getGame(gameid) match {
      case Some(game) => game.players = game.players.filterNot { x => x.id == playerid }
      case None => println("Error removePlayer")
    }
  }

  //@TODO ?
  //woher weiss der Spieler das er gejoint ist? Maximum festlegen?
  def joinGame(gameid: String, _name: String, _uri: String) {
    var player = Player(id = _name.toLowerCase, name = _name, uri = _uri)
    getGame(gameid) match {
      case Some(game) =>
        println("player joinGame")
        game.players +:= player
      case None => println("Error joinGame")
    }
  }

  def isPlayerReady(gameid: String, playerid: String): Boolean = {
    getPlayer(gameid, playerid) match {
      case Some(player) =>
        println("is player ready")
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
                    player.ready = true
                    game.players.head.ready = false
                    setMutex(gameid, player.id)
                  case None => None
                }
              }
            case None =>
          }
        } else {
          //Game not started lobby
          println("setPlayerReady lobby success")
          playerTry.ready = true

          //@TODO
          //check if all players ready => start game
          val players = getPlayers(gameid)
          if (players.forall(_.ready == true)) {
            println("setPlayerReady game started")
            //@TODO hier fehlt was, direktes starten des spiels nicht erlaubt, da ready true erwartet wird
            //players.head.ready = false
            getGame(gameid).get.started = true
          }
        }
      case None => println("Error setPlayerReady")
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
      case Some(game) => Some(game.mutex)
      case None => None
    }
  }

  //@TODO Zu aquiere umbennen?
  def setMutex(gameid: String, playerid: String): String = {
    //@TODO Hier einen echten mutex einbauen, da jetty async?
    getGame(gameid) match {
      case Some(game) =>
        if (playerid == game.mutex) {
          "200"
        } else if (game.mutex == "") {
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
      case None => println("Error resetMutex")
    }
  }

  def resetGames(): Unit = {
    games = Map()
    _id = 0
  }

  def apply() = new Games(games.values.toList) //getGames
}

case class Components(game: String, dice: String, board: String, bank: String, broker: String, decks: String, events: String)

case class Games(games: List[Game])

//@TODO
//get /boards wieso enthält ein Game kein ready und players?
case class Game(gameid: String = Games.id().toString, var players: List[Player] = List(), components: Components, var started: Boolean = false, var mutex: String = "")