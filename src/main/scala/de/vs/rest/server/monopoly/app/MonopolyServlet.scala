package de.vs.rest.server.monopoly.app

import org.scalatra._
import scalate.ScalateSupport

import org.json4s._
import org.json4s.JsonDSL._
import org.scalatra.json.{ JValueResult, JacksonJsonSupport }

import de.vs.monopoly.Dice
import de.vs.monopoly.Games

class MonopolyServlet extends ScalatraServlet with ScalateSupport with JacksonJsonSupport {

  protected implicit val jsonFormats: Formats = DefaultFormats

  before() {
    contentType = formats("json")
    response.headers += ("Access-Control-Allow-Origin" -> "*")
  }

  //Gives you a single dice roll
  get("/dice") {
    Dice() roll
  }

  //Gives you a List of all Games
  get("/games") {
    Games()
  }
  
  //Creates a new Game
  post("/games") {
    Created( Games createNewGame )
  }
  
  //Creates a new Game
  get("/games/:gameid") {
    Games getGame(params("gameid")) match {
      case Some(game) => game
      case None => //Gibts nicht?
    }
  }
  
}
