package de.vs.rest.server.monopoly.app

import org.scalatra._
import scalate.ScalateSupport

import org.json4s._
import org.json4s.JsonDSL._
import org.scalatra.json.{JValueResult, JacksonJsonSupport}

import de.vs.monopoly._

class GameServlet extends ScalatraServlet with ScalateSupport with JacksonJsonSupport {

  protected implicit val jsonFormats: Formats = DefaultFormats

  protected override def transformRequestBody(body: JValue): JValue = body.camelizeKeys

  before() {
    contentType = formats("json")
    response.headers += ("Access-Control-Allow-Origin" -> "*")
  }

  //GAMES
  //Gives you a List of all Games
  get("/") {
    Games()
  }

  //Creates a new Game
  post("/") {
    Created(Games createNewGame)
  }

  //get a Game by gameid
  get("/:gameid") {
    Games getGame (params("gameid")) match {
      case Some(game) => game
      case None => //Gibts nicht?
    }
  }

  //get all players
  get("/:gameid/players") {
    Games getGame (params("gameid")) match {
      case Some(game) => game.players
      case None => //Gibts nicht?
    }
  }

  //get player
  get("/:gameid/players/:playerid") {
    Games getGame (params("gameid")) match {
      case Some(game) =>
        game.players.find { x => x.id == params("playerid") } match {
          case Some(player) => player
          case None => //Gibts nicht?
        }
      case None => //Gibts nicht?
    }
  }

  //start game
  put("/:gameid/start") {
    Games startGame (params("gameid"))
  }

  //put player to game(join game)
  put("/:gameid/players/:playerid") {
    Games joinGame(params("gameid"), params("name"), params("uri"))
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
      case None => //Gibts nicht?
    }
  }

  //gets the player holding the mutex
  get("/:gameid/players/turn") {
    Games getMutex (params("gameid")) match {
      case Some(player) => player
      case None => //Gibts nicht?
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
      case None => //Gibts nicht?
    }
  }

  //get a community
  get("/:gameid/community") {
    Decks community() match {
      case Some(card) => card
      case None => //Gibts nicht?
    }
  }
}
