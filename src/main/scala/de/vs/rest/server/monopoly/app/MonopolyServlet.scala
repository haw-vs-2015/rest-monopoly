package de.vs.rest.server.monopoly.app

import org.scalatra._
import scalate.ScalateSupport

import org.json4s._
import org.json4s.JsonDSL._
import org.scalatra.json.{ JValueResult, JacksonJsonSupport }

import de.vs.monopoly.Dice
import de.vs.monopoly.Games
import de.vs.monopoly.Decks
import de.vs.monopoly.Board
import de.vs.monopoly.Boards
import de.vs.monopoly.Throw

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

  //BOARDS
  get("/boards") {
    Boards()
  }

  get("/boards/{gameid}") {
    Boards.gameBoard(params("gameid")) match {
      case Some(board) => board
      case None => //Gibts nicht?
    }
  }
  
  put("/boards/{gameid}") {
    Boards.addBoard(params("gameid"))
  }
  
  delete("/boards/{gameid}") {
    Boards.deleteBoard(params("gameid"))
  }

  post("/boards/:gameid/players/:playerid/roll") {
    //hier fehlt noch was
    params("gameid")
    params("playerid")
    Boards.rolled(parsedBody.extract[Throw])
  }
  
  get("/boards/{gameid}/players") {
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
