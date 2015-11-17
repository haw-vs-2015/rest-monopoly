package de.vs.rest.server.monopoly.app

import de.vs.monopoly._
import org.json4s._
import org.scalatra._
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.scalate.ScalateSupport

class DiceServlet extends ScalatraServlet with ScalateSupport with JacksonJsonSupport {

  protected implicit val jsonFormats: Formats = DefaultFormats

  protected override def transformRequestBody(body: JValue): JValue = body.camelizeKeys

  before() {
    contentType = formats("json")
    response.headers += ("Access-Control-Allow-Origin" -> "*")
  }

  //DICE
  //Gives you a single dice roll
  get("/") {
    Dice() roll
  }
}
