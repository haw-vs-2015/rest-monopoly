package de.vs.monopoly.logic

import play.api.Logger


object Games {
  //GamesFacade
  //variables
  var _id = 0
  var games: Map[String, Game] = Map()
  var gamesURI: Map[String, GameURI] = Map()

  var players: Map[String, List[Player]] = Map()
  var playerReady: Map[String, Boolean] = Map()

  //methods
  def id(): String = {
    _id += 1
    _id.toString
  }

  def createNewGame(host: String, port: String): Game = {

    val game = Game()
    game.uri = "http://" + host + ":" + port + "/games/" + game.gameid
    game.players = game.uri + "/players" + "/"

    //Init Components //Generate Components fehlt
    val _game = game.uri
    val dice = "http://" + host + ":" + port + "/dice" + "/"
    val board = "http://" + host + ":" + port + "/boards/" + game.gameid
    val bank = ""
    val broker = ""
    val decks = ""
    val events = ""
    val _components = Components(_game, dice, board, bank, broker, decks, events)

    game.components = _components
    players += (game.gameid -> List())
    games += (game.gameid -> game)
    gamesURI += (game.gameid -> GameURI(game.gameid, game.started, game.uri))

    Logger.info("created game " + game.gameid)
    game
  }

  //dirty
  def findPlayer(playerid: String): Option[Player] = {
    var rs: Option[Player] = None
    for (game <- games.values) {
      players(game.gameid).find(p => p.id == playerid) match {
        case Some(player) => rs = Some(player)
        case None =>
      }
    }
    rs
  }

  def getGame(gameid: String): Option[Game] = {
    games.get(gameid)
  }

  def getGameURI(gameid: String): Option[GameURI] = {
    gamesURI.get(gameid)
  }

  def getPlayer(gameid: String, playerid: String): Option[Player] = {
    players.get(gameid) match {
      case Some(_players) =>
        Logger.info("game " + gameid + " found.")
        _players.find(x => x.id == playerid)
      case None =>
        Logger.info("game" + gameid + " missing.")
        None
    }
  }

  //@TODO event to board remove player?
  def removePlayer(gameid: String, playerid: String) {
    players.get(gameid) match {
      case Some(_players) =>
        val old = _players
        players += (gameid -> _players.filterNot { x => x.id == playerid })
        Logger.info("player " + playerid + " is not in the game anymore.")
        //remove game if has no players
        //@TODO check has changed needed??
        if (_players.size < old.size && _players.isEmpty) {
          removeGame(gameid)
          Logger.info("game empty and removed " + gameid)
        }
      case None =>
        Logger.info("Error removePlayer " + playerid + " in game" + gameid + " game not found.")

    }
  }

  def removeGame(gameid: String): Unit = {
    games -= gameid
    gamesURI -= gameid
  }

  //@TODO ?
  //woher weiss der Spieler das er gejoint ist? Maximum festlegen?
  def joinGame(host: String, port: String, gameid: String, _id: String, _name: String, _uri: String): Option[Player] = {

    val _uri = "http://" + host + ":" + port + "/games/" + gameid + "/players/" + _id + "/"
    val readyURI = "http://" + host + ":" + port + "/games/" + gameid + "/players/" + _id + "/ready"

    var player = Player(id = _id, name = _name, gameid = gameid, uri = _uri, ready = readyURI)

    getGame(gameid) match {
      case Some(game) =>
        if (!game.started) {
          Logger.info("player000 " + player.id + " joined Game " + gameid)

          var l = players.get(gameid).get
          Logger.info("JOINED1: " + players)
          l :+= player

          Logger.info("JOINED2: " + players)
          players += (gameid -> l)
          playerReady += (_id -> false)
          Logger.info("JOINED3: " + players)
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
        playerReady.get(player.id).get
      case None => false
    }
  }

  //Spielerwechsel? und lobby ready
  def setPlayerReady(gameid: String, playerid: String) {

    getPlayer(gameid, playerid) match {
      case Some(_playerReady) =>
        //Game started
        if (getGame(gameid).get.started) {
          getCurrentPlayer(gameid) match {
            case Some(player) =>
              if (player.id == playerid) {
                //check ob spieler gerade dran auch der jenige, der ready drueckt
                getGame(gameid) match {
                  case Some(game) =>
                    //@TODO head option einbauen
                    players += (gameid -> ((players.get(gameid).get).tail :+ (players.get(gameid).get).head))
                    playerReady += (player.id -> true)
                    playerReady += ((players.get(gameid).get)(0).id -> false)
                    //Logger.info("setPlayerReady " + player.id + " is ready now")
                    resetMutex(gameid)
                    setMutex(gameid, (players.get(gameid).get)(0).id)
                  case None => None
                }
              }
            case None => None
          }
        } else {
          //Game not started lobby
          Logger.info("setPlayerReady " + playerid + " lobby success")
          playerReady += (playerid -> true)
        }
      case None => Logger.info("Error " + playerid + " setPlayerReady")
    }
  }

  def startGame(gameid: String): Option[Player] = {
    //check if all players ready => start game
    val players = getPlayers(gameid).getOrElse(Nil)
    Logger.info("try start " + gameid + " game")
    //    players.foreach(p => println(p.ready))
    if (playerReady.values.forall(_ == true)) {
      Logger.info("setPlayerReady game started")
      //@TODO Schon fertig?  - hier fehlt was, direktes starten des spiels nicht erlaubt, da ready true erwartet wird
      playerReady += (players.head.id -> false)
      setMutex(gameid, players.head.id)
      getGame(gameid).get.started = true
      getGameURI(gameid).get.started = true
      Logger.info("game " + gameid + " erfolgreich gestartet")
      Some(players.head)
    } else {
      Logger.info("One Player or more players are not ready " + gameid)
      None
    }
  }

  def getPlayers(gameid: String): Option[List[Player]] = {
    players.get(gameid)
  }

  def getPlayersURI(host: String, port: String, gameid: String): Option[List[PlayerURI]] = {
    players.get(gameid) match {
      case Some(_players) =>
        var rs = List[PlayerURI]()
        for (player <- _players) {
          val uri = "http://" + host + ":" + port + "/games/" + gameid + "/player/" + player.id
          rs :+= PlayerURI(player.id, player.name, player.gameid, uri)
        }
        Some(rs)
      case None => None
    }
  }

  //@TODO ?
  //Die reihenfolge muss implementiert werden letzter in der Liste wird an
  //stelle eins gesetzt, wenn er dran war.
  def getCurrentPlayer(gameid: String): Option[Player] = {
    players.get(gameid) match {
      case Some(_players) => _players.headOption
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
    gamesURI = Map()
    players = Map()
    _id = 0
    Players._id = 0
  }

  def getGames(): List[Game] = {
    games.values.toList
  }

  def getGamesAsURI(): List[GameURI] = {
    gamesURI.values.toList
  }

  def apply() = new GamesURI(getGamesAsURI()) //getGames
}


case class Components(var game: String, var dice: String, var board: String, var bank: String, var broker: String, var decks: String, var events: String)

case class Games(games: List[Game])

case class GamesURI(games: List[GameURI])

case class GameURI(name: String, var started: Boolean, uri: String)

//@TODO
//get /boards wieso enthält ein Game kein ready und players?
case class Game(gameid: String = Games.id().toString, var players: String = "", var components: Components = Components("", "", "", "", "", "", ""), var started: Boolean = false, var mutex: String = "", var uri: String = "")

