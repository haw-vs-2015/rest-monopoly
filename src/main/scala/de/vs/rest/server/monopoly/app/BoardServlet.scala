package de.vs.rest.server.monopoly.app

import de.vs.monopoly._
import org.json4s._
import org.scalatra._
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.scalate.ScalateSupport

import scala.concurrent.duration._
import de.vs.http.client.Http

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
    Boards.get(params("gameid")) match {
      case Some(board) => board
      case None => NotFound()
    }
  }

  put("/:gameid") {
    Boards.addBoard(params("gameid")) match {
      case Some(text) => Created()
      case None => Conflict()
    }
  }

  delete("/:gameid") {
    Boards.deleteBoard(params("gameid")) match {
      case Some(text) => Ok()
      case None => NotFound()
    }
  }

  //get all players on board
  get("/:gameid/players") {
    Boards.getPlayers(params("gameid"))
  }

  //join player
  put("/:gameid/players/:playerid") {
    Boards.putPlayerToBoard(params("gameid"), params("playerid")) match {
      case Some(playerid) =>

        Created()
      case None => NotFound()
    }
  }

  //get player
  get("/:gameid/players/:playerid") {
    Boards.getPlayer(params("gameid"), params("playerid")) match {
      case Some(player) => player
      case None => NotFound()
    }
  }

  //delete player
  delete("/:gameid/players/:playerid") {
    Boards.deletePlayer(params("gameid"), params("playerid")) match {
      case Some(text) => Ok()
      case None => NotFound()
    }
  }

  post("/:gameid/players/:playerid/roll") {
    //@TODO Sollte fertig sein
    val _throw = parsedBody.extract[Throw]

    var reponse = Http.get("/games/" + params("gameid") + "/players/turn", 2 seconds)
    if (reponse.status == 200) {
      var playerMap = parse(reponse.body).extract[Map[String, String]]

      playerMap.get("id") match {
        case Some(currPlayerid) =>
          Boards.rolled(params("gameid"), params("playerid"), currPlayerid, _throw)
          Created()
        case None => NotFound()
      }
    }
  }
}
