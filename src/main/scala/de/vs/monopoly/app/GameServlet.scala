package de.vs.monopoly.app

import de.alexholly.util.http.{HttpAsync, HttpSync}
import de.alexholly.util.tcpsocket.ServerKomponenteFacade
import de.vs.monopoly.logic._

import org.scalatra._
import org.scalatra.json.JacksonJsonSupport
import org.json4s._
import org.json4s.native.Serialization.{read, write}
import play.api.Logger
import scalate.ScalateSupport
import play.api.libs.ws.WSResponse
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

class GameServlet extends ScalatraServlet with ScalateSupport with JacksonJsonSupport {

  protected implicit val jsonFormats: Formats = DefaultFormats

  protected override def transformRequestBody(body: JValue): JValue = body.camelizeKeys

  val TIMEOUT = 5000

  before() {
    contentType = formats("json")
    response.headers += ("Access-Control-Allow-Origin" -> "*")
  }

  //@TODO Not found liefern bei fails
  //GAMES
  //Gives you a List of all Games
  get("/") {
    Games()
  }

  //Creates a new Game
  post("/") {
    //@TODO game nur erzeugen wenn auch board erzeugt wurde
    val game = Games createNewGame(request.getLocalAddr(), request.getLocalPort().toString)
    //@TODO Ask yellowpages for boards service url
    var response: WSResponse = null

    Logger.info("1: " + Global.boards_uri)
    response = HttpSync.put(Global.boards_uri + "/boards/" + game.gameid, TIMEOUT)

    if (response.status == 201) {
      Logger.info("2: ")
      //@TODO yellowpages funkioniert noch nicht
      updateGames()
      Ok("response", Map("Location" -> game.uri))
    } else {

      Logger.info("3: ")
      Games.removeGame(game.gameid)
      NotFound()
    }
  }

  //get a Game by gameid
  get("/:gameid") {
    Games getGame (params("gameid")) match {
      case Some(game) => game
      case None => NotFound()
    }
  }

  //get all players
  get("/:gameid/players") {
    Games.getPlayersURI(request.getLocalAddr(), request.getLocalPort().toString, params("gameid")) match {
      case Some(playersUri) => playersUri
      case None => NotFound()
    }
  }

  //get player
  get("/:gameid/players/:playerid") {
    Games.getPlayer(params("gameid"), params("playerid")) match {
      case Some(player) => player
      case None => NotFound()
    }
  }

  //start game gibts nicht in der api
  put("/:gameid/start") {
    Games startGame (params("gameid")) match {
      case Some(player) =>
        //@TODO Wenn hier put anstatt post genutzt wird, erhält man ein komisches verhalten.
        //@TODO Wie fängt man jetty exceptions ab? Wird das überhaupt benötigt?

        //@TODO einfache alle player updaten?

        startGame(params("gameid"), player.id)

      case None => Forbidden()
    }
  }

  def startGame(gameid: String, playerid: String) {
    var message = Message("SERVER", "player_turn", "EGAL", "")
    val post = "POST /messages/send/" + gameid + " HTTP/1.1\r\n" + "Content-Type: application/json; charset=UTF-8\r\n\r\n" + write(message)
    ServerKomponenteFacade.senden(playerid, post)

    message = Message("SERVER", "start_game", "EGAL", "")
    HttpAsync.post("http://localhost:4567/messages/send/" + gameid, write(message))
  }

  //@TODO game joinen nur moeglich, wenn nicht bereits gejoint
  //put player to game(join game)
  put("/:gameid/players/:playerid") {
    //    print("http://" + request.getRemoteAddr + ":3560")
    //    print("http://" + request.getRequestURI + ":3560")
    //    print("http://" + request.getRequestURL + ":3560")
    //    print("http://" + request.getRemoteAddr + ":3560")
    //    print("http://" + request.getRemoteHost + ":3560")
    var uri = "http://" + request.getRemoteAddr + ":3560"

    //entfernen des spielers aus dem letzten game das er gejoint hat
    //@TODO sieht dirty aus
    HttpSync.delete("http://localhost:4567" + "/games/" + params("gameid") + "/players/" + request.getRemoteAddr, TIMEOUT)

    Games joinGame(request.getLocalAddr(), request.getLocalPort().toString, params("gameid"), request.getRemoteAddr, params("name"), uri) match {
      case Some(player) =>
        //put player on board
        var response: WSResponse = null
        response = HttpSync.put(Global.boards_uri + "/boards/" + params("gameid") + "/players/" + request.getRemoteAddr, TIMEOUT)
        if (response.status == 201) {
          //Dem spieler den game channel subscriben
          val subscriber = Subscriber(player.id, Nil, "")
          response = HttpSync.post(Global.messages_uri + "/messages/subscribe/" + params("gameid"), write(subscriber), TIMEOUT)
          //sagen das ein neuer spieler gejoint ist(Service event)
          updatePlayers(params("gameid"))
          Ok(player)
        } else {
          Games.removePlayer(params("gameid"), params("playerid"))
          NotFound()
        }
      case None =>
        NotFound()
    }
  }

  //@TODO tests muessen geschrieben werden
  //@TODO events fehlen
  //remove player
  delete("/:gameid/players/:playerid") {
    //print(request.getRemoteHost)
    Games removePlayer(params("gameid"), params("playerid"))

    //Remove player from board
    var response: WSResponse = null
    response = HttpSync.delete(Global.boards_uri + "/boards/" + params("gameid") + "/players/" + params("playerid"), TIMEOUT)
    HttpAsync.delete(Global.messages_uri + "/messages/" + params("gameid") + "/subscriber/" + params("playerid"))

    //If no players in game anymore remove the board
    Games.getGame(params("gameid")) match {
      case Some(game) => updatePlayers(params("gameid"))
      case None => //Remove game
        response = HttpSync.delete(Global.boards_uri + "/boards/" + params("gameid"), TIMEOUT)
        updateGames()
    }
  }

  def updateGames(): Unit = {
    val message = Message("SERVER", "update_games", "EGAL", write(Games()))
    //println("games: " + write(Games()))
    HttpSync.post(Global.messages_uri + "/messages/send/update_games", write(message), TIMEOUT)
  }

  //is player ready
  get("/:gameid/players/:playerid/ready") {
    Games isPlayerReady(params("gameid"), params("playerid"))
  }

  //set player "ready status"
  put("/:gameid/players/:playerid/ready") {
    Games setPlayerReady(params("gameid"), params("playerid"))
    updatePlayers(params("gameid"))
  }

  def updatePlayers(gameid: String): Unit = {
    val players = Players(Games.getPlayers(gameid).get)
    val message = Message("SERVER", "update_players", "EGAL", write(players))
    HttpAsync.post(Global.messages_uri + "/messages/send/" + gameid, write(message))
  }

  //get player of current turn
  get("/:gameid/players/current") {
    Games getCurrentPlayer (params("gameid")) match {
      case Some(player) => player //json fuegt keine " "hinzu
      case None => NotFound()
    }
  }

  //gets the player holding the mutex
  get("/:gameid/players/turn") {
    Games getMutex (params("gameid")) match {
      case Some(playerid) =>
        playerid
      case None =>
        NotFound()
    }
  }

  //@TODO ?
  //Nur nicht nur die Player id uebergeben? id muss noch aus body geholt werden.
  //put tries to aquire the turn mutex
  put("/:gameid/players/:playerid/turn") {
    Games setMutex(params("gameid"), params("playerid")) match {
      case "200" => Ok()
      case "201" => Created()
      case "409" => Conflict()
    }
  }

  //delete the mutex
  delete("/:gameid/players/turn") {
    Games resetMutex (params("gameid"))
  }

  //DECKS
  //@TODO ?
  //Muss irgendwie mit game(s) verbunden werden.. Ein game hat Decks, wo steht das in der api?
  //get a chance
  get("/:gameid/chance") {
    Decks chance() match {
      case Some(card) => card
      case None => NotFound()
    }
  }

  //get a community
  get("/:gameid/community") {
    Decks community() match {
      case Some(card) => card
      case None => NotFound()
    }
  }

}
