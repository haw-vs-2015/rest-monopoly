package de.vs.monopoly.app

import de.alexholly.util.http.HttpAsync
import de.vs.monopoly.logic.{Games, Player, Place}
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
    Player("testPlayer", "TestPlayer", "1", "http://localhost:4567", Place(), 0, "")
  }

  //Informs the player, that it is his turn
  post("/turn") {
    println("MyTurn")
    Ok()
  }

  //find player
  delete("/:playerid") {
    Games findPlayer (params("playerid")) match {
      case Some(player) =>
        HttpAsync.delete("http://localhost:4567" + "/games/" + player.gameid + "/players/" + player.id)
        Ok()
      case None => NotFound()
    }
  }

  //inform a player about a new event
  post("/event") {
    println("SomeEvent")
    Ok()
  }
}
