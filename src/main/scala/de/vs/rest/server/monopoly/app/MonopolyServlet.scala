package de.vs.rest.server.monopoly.app

import org.scalatra._
import scalate.ScalateSupport

import org.json4s._
import org.json4s.JsonDSL._
import org.scalatra.json.{ JValueResult, JacksonJsonSupport }

import de.vs.monopoly._

class MonopolyServlet extends ScalatraServlet with ScalateSupport with JacksonJsonSupport {

  protected implicit val jsonFormats: Formats = DefaultFormats
  protected override def transformRequestBody(body: JValue): JValue = body.camelizeKeys

  before() {
    contentType = formats("json")
    response.headers += ("Access-Control-Allow-Origin" -> "*")
  }

  //DICE
  //Gives you a single dice roll
  get("/dice") {
    Dice() roll
  }

  //GAMES
  //Gives you a List of all Games
  get("/games") {
    Games()
  }

  //Creates a new Game
  post("/games") {
    Created(Games createNewGame)
  }

  //Creates a new Game
  get("/games/:gameid") {
    Games getGame (params("gameid")) match {
      case Some(game) => game
      case None => //Gibts nicht?
    }
  }

  //get all players
  get("/games/:gameid/players") {
    Games getGame (params("gameid")) match {
      case Some(game) => game.players
      case None => //Gibts nicht?
    }
  }

  //get player
  get("/games/:gameid/players/:playerid") {
    Games getGame (params("gameid")) match {
      case Some(game) =>
        game.players.find { x => x.id == params("playerid") } match {
          case Some(player) => player
          case None => //Gibts nicht?
        }
      case None => //Gibts nicht?
    }
  }

  //put player to game(join game)
  put("/games/:gameid/players/:playerid/:uri") {
    Games joinGame (params("gameid"), params("playerid"), params("uri"))
  }

  //remove player
  delete("/games/:gameid/players/:playerid") {
    Games removePlayer (params("gameid"), params("playerid"))
  }

  //is player ready
  get("/games/:gameid/players/:playerid/ready") {
    Games isPlayerReady (params("gameid"), params("playerid"))
  }

  //set player "ready status"
  put("/games/:gameid/players/:playerid/ready") {
    Games setPlayerReady (params("gameid"), params("playerid"))
  }

  //get player of current turn
  get("/games/:gameid/players/current") {
    Games getCurrentPlayer (params("gameid")) match {
      case Some(player) => player
      case None => //Gibts nicht?
    }
  }

  //gets the player holding the mutex
  get("/games/:gameid/players/turn") {
    Games getMutex (params("gameid")) match {
      case Some(player) => player
      case None => //Gibts nicht?
    }
  }

  //Nur nicht nur die Player id uebergeben? id muss noch aus body geholt werden.
  //put tries to aquire the turn mutex
  put("/games/:gameid/players/turn") {
    
    Games setMutex (params("gameid"), params("playerid")) match {
      case "200" => Ok()
      case "201" => Created()
      case "409" => Gone()
    }
  }

  //delete the mutex
  delete("/games/:gameid/players/turn") {
    Games setMutex (params("gameid"), params("playerid"))
  }

  //BOARDS
  get("/boards") {
    Boards()
  }

  get("/boards/:gameid") {
    Boards.gameBoard(params("gameid")) match {
      case Some(board) => board
      case None => //Gibts nicht?
    }
  }

  put("/boards/:gameid") {
    Boards.addBoard(params("gameid"))
  }

  delete("/boards/:gameid") {
    Boards.deleteBoard(params("gameid"))
  }

  post("/boards/:gameid/players/:playerid/roll") {
    //hier fehlt noch was
    params("gameid")
    params("playerid")
    Boards.rolled(parsedBody.extract[Throw])
  }

  get("/boards/:gameid/players") {
    Boards.getPlayers(params("gameid"))
  }

  //DECKS
  //Muss irgendwie mit game(s) verbunden werden.. Ein game hat Decks, wo steht das in der api?
  //get a chance
  get("/games/:gameid/chance") {
    Decks chance () match {
      case Some(card) => card
      case None => //Gibts nicht?
    }
  }

  //get a community
  get("/games/:gameid/community") {
    Decks community () match {
      case Some(card) => card
      case None => //Gibts nicht?
    }
  }
}
