package de.vs.rest.server.monopoly.app

import de.vs.monopoly._
import org.json4s._
import org.scalatra._
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.scalate.ScalateSupport
import play.api.libs.ws.WSResponse

import scala.concurrent.duration._
import de.alexholly.util.http.HttpSync

class BoardServlet extends ScalatraServlet with ScalateSupport with JacksonJsonSupport {

  protected implicit val jsonFormats: Formats = DefaultFormats

  protected override def transformRequestBody(body: JValue): JValue = body.camelizeKeys

  val TIMEOUT = 2 seconds

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
    println("1")
    var response: WSResponse = null
    if (Global.testMode) {
      println("2")
      response = HttpSync.get(Global.default_url + "/games/" + params("gameid") + "/players/turn", TIMEOUT)
    } else {
      response = HttpSync.get("http://localhost:4567" + "/games/" + params("gameid") + "/players/turn", TIMEOUT)
    }
    println("3")
    if (response.status == 200) {
      println(response.body)
      var currPlayerid = response.body
      println(currPlayerid)
      println(request.body)
      val _throw = parse(request.body).extract[Throw]
      println(_throw)
      Boards.rolled(params("gameid"), params("playerid"), currPlayerid, _throw) match {
        case Some(boardstatus) =>
          println("gerollt")
          boardstatus
        case None =>
          println("roll error")
          NotFound()
      }
    } else {
      println("4 " + response.status)
      response.status
    }
  }
}