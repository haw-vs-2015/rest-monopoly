package de.vs.rest.server.monopoly.app

import de.vs.monopoly._
import org.json4s._
import org.scalatra._
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.scalate.ScalateSupport

class BoardServlet extends ScalatraServlet with ScalateSupport with JacksonJsonSupport {

  protected implicit val jsonFormats: Formats = DefaultFormats

  protected override def transformRequestBody(body: JValue): JValue = body.camelizeKeys

  before() {
    contentType = formats("json")
    response.headers += ("Access-Control-Allow-Origin" -> "*")
  }

  //BOARDS
  get("/") {
    Boards()
  }

  get("/:gameid") {
    Boards.gameBoard(params("gameid")) match {
      case Some(board) => board
      case None => //Gibts nicht?
    }
  }

  put("/:gameid") {
    Boards.addBoard(params("gameid"))
  }

  delete("/:gameid") {
    Boards.deleteBoard(params("gameid"))
  }

  post("/:gameid/players/:playerid/roll") {
    //@TODO Sollte fertig sein
    val _throw = parsedBody.extract[Throw]
    Boards.rolled(params("gameid"), params("playerid"), _throw)
  }

  get("/:gameid/players") {
    Boards.getPlayers(params("gameid"))
  }
}
