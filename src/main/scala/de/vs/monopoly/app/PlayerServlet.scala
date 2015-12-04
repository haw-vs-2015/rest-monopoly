package de.vs.monopoly.app

import de.vs.monopoly.logic.Player
import de.vs.monopoly.logic.Place
import org.json4s._
import org.scalatra._
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.scalate.ScalateSupport

class PlayerServlet extends ScalatraServlet with ScalateSupport with JacksonJsonSupport {

  protected implicit val jsonFormats: Formats = DefaultFormats

  protected override def transformRequestBody(body: JValue): JValue = body.camelizeKeys

  before() {
    contentType = formats("json")
    response.headers += ("Access-Control-Allow-Origin" -> "*")
  }

  //gets the details about the player
  get("/") {
    Player("testPlayer", "TestPlayer", "http://localhost:4567", Place(), 0, false)
  }

  //Informs the player, that it is his turn
  post("/turn") {
    println("MyTurn")
    Ok()
  }

  //inform a player about a new event
  post("/event") {
    println("SomeEvent")
    Ok()
  }
}
