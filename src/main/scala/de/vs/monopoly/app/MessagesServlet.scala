package de.vs.monopoly.app

import de.vs.monopoly.logic.Messages
import org.json4s._
import org.scalatra._
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.scalate.ScalateSupport

class MessagesServlet extends ScalatraServlet with ScalateSupport with JacksonJsonSupport {

  protected implicit val jsonFormats: Formats = DefaultFormats

  protected override def transformRequestBody(body: JValue): JValue = body.camelizeKeys

  before() {
    contentType = formats("json")
    response.headers += ("Access-Control-Allow-Origin" -> "*")
  }

  //gets all Channels and subsribers for the channels
  get("/") {
    Messages.getChannels()
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
