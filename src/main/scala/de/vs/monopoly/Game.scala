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
        println("game " + gameid + " found.")
        game.players.find(x => x.id == playerid)
      case None =>
        println("game" + gameid + " missing.")
        None
    }
  }

  //@TODO event to board remove player?
  def removePlayer(gameid: String, playerid: String) {
    getGame(gameid) match {
      case Some(game) =>
        val old = game.players
        game.players = game.players.filterNot { x => x.id == playerid }
        println("player " + playerid + " is not in the game anymore.")
        //remove game if has no players
        if (game.players.size < old.size && game.players.isEmpty) {
          removeGame(gameid)
          println("game empty and removed " + gameid)
        }
      case None => println("Error removePlayer " + playerid + " in game" + gameid)
    }
  }

  def removeGame(gameid: String): Unit = {
    games -= gameid
  }

  //@TODO ?
  //woher weiss der Spieler das er gejoint ist? Maximum festlegen?
  def joinGame(gameid: String, _name: String, _uri: String): Option[Player] = {
    var player = Player(id = _name.toLowerCase, name = _name, uri = _uri)
    getGame(gameid) match {
      case Some(game) =>
        println("player " + player.id + " joined Game " + gameid)
        game.players +:= player
        Some(player)
      case None => println("Error " + gameid + " joinGame"); None
    }
  }

  def isPlayerReady(gameid: String, playerid: String): Boolean = {
    getPlayer(gameid, playerid) match {
      case Some(player) =>
        println("player " + playerid + " is ready")
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
                    setMutex(gameid, player.id)
                  case None => None
                }
              }
            case None =>
          }
        } else {
          //Game not started lobby
          println("setPlayerReady " + playerid + " lobby success")
          playerTry.ready = true
        }
      case None => println("Error " + playerid + " setPlayerReady")
    }
  }

  def startGame(gameid: String): Option[Player] = {
    //check if all players ready => start game
    val players = getPlayers(gameid)
    println("try start " + gameid + " game")
    //    players.foreach(p => println(p.ready))
    if (players.forall(_.ready == true)) {
      println("setPlayerReady game started")
      //@TODO hier fehlt was, direktes starten des spiels nicht erlaubt, da ready true erwartet wird
      players.head.ready = false
      setMutex(gameid, players.head.id)
      getGame(gameid).get.started = true
      println("game " + gameid + " erfolgreich gestartet")
      Some(players.head)
    } else {
      println("One Player or more players are not ready " + gameid)
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
        println("getMutex game " + gameid)
        if (game.mutex != "") {
          println("getMutex game " + gameid + " mutex ist belegt " + game.mutex)
          Some(game.mutex)
        }else {
          None
        }
      case None => None
    }
  }

  //@TODO Zu aquiere umbennen?
  def setMutex(gameid: String, playerid: String): String = {
    //@TODO Hier einen echten mutex einbauen, da jetty async?
    getGame(gameid) match {
      case Some(game) =>
        if (playerid == game.mutex) {
          println(playerid + " hat den Mutex bereits")
          "200"
        } else if (game.mutex == "") {
          println(playerid + " hat den Mutex bekommen")
          game.mutex = playerid
          "201"
        } else {
          println("jemand anderes hat den Mutex")
          "409"
        }
      case None =>
        println("mutex setzen, game nicht gefunden")
        ""
    }
  }

  def resetMutex(gameid: String) {
    getGame(gameid) match {
      case Some(game) => game.mutex = ""
      case None => println("Error game " + gameid + " resetMutex")
    }
  }

  def reset(): Unit = {
    games = Map()
    _id = 0
  }

  def apply() = new Games(games.values.toList) //getGames
}


case class Components(var game: String, var dice: String, var board: String, var bank: String, var broker: String, var decks: String, var events: String)

case class Games(games: List[Game])

//@TODO
//get /boards wieso enthält ein Game kein ready und players?
case class Game(gameid: String = Games.id().toString, var players: List[Player] = List(), components: Components, var started: Boolean = false, var mutex: String = "")