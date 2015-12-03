package de.vs.rest.server.monopoly.app

import org.scalatra._
import scalate.ScalateSupport

import org.json4s._
import org.scalatra.json.JacksonJsonSupport

import play.api.libs.ws.WSResponse
import de.vs.monopoly._
import scala.concurrent.duration._
import de.alexholly.util.http.HttpSync

class GameServlet extends ScalatraServlet with ScalateSupport with JacksonJsonSupport {

  protected implicit val jsonFormats: Formats = DefaultFormats

  protected override def transformRequestBody(body: JValue): JValue = body.camelizeKeys

  val TIMEOUT = 2 seconds

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
    var game = Games createNewGame()
    //@TODO Ask yellowpages for boards service url
    var response: WSResponse = null
    if (Global.testMode) {
      response = HttpSync.put(Global.default_url + "/boards/" + game.gameid, TIMEOUT)
    } else {
      response = HttpSync.put("http://localhost:4567" + "/boards/" + game.gameid, TIMEOUT)
    }
    if (response.status == 201) {
      game.components.board = "http://localhost:4567/boards"
      Created(game)
    } else {
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
    Games getGame (params("gameid")) match {
      case Some(game) => game.players
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
        println("tell player " + player.id + " it's his turn.")
        println(player.uri + "/player/turn")
        //@TODO Wenn hier put anstatt post genutzt wird, erhält man ein komisches verhalten.

        HttpSync.post(player.uri + "/player/turn", TIMEOUT)
      case None => Forbidden()
    }
  }

  //put player to game(join game)
  put("/:gameid/players/:playerid") {
    Games joinGame(params("gameid"), params("name"), params("uri")) match {
      case Some(player) =>
        //put player on board
        var response: WSResponse = null
        if (Global.testMode) {
          response = HttpSync.put(Global.default_url + "/boards/" + params("gameid") + "/players/" + player.id.toLowerCase, TIMEOUT)
        } else {
          response = HttpSync.put("http://localhost:4567" + "/boards/" + params("gameid") + "/players/" + player.id.toLowerCase, TIMEOUT)
        }
        if (response.status == 201) {
          println("player ok")
          Ok(player)
        } else {
          Games.removePlayer(params("gameid"), params("playerid"))
          NotFound()
        }
      case None =>
        println("no player")
        NotFound()
    }
  }

  //remove player
  delete("/:gameid/players/:playerid") {
    Games removePlayer(params("gameid"), params("playerid"))
  }

  //is player ready
  get("/:gameid/players/:playerid/ready") {
    Games isPlayerReady(params("gameid"), params("playerid"))
  }

  //set player "ready status"
  put("/:gameid/players/:playerid/ready") {
    Games setPlayerReady(params("gameid"), params("playerid"))
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
        println("turn liefert " + playerid)
        playerid
      case None =>
        println("niemand hat den turn")
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
