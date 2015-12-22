package de.vs.monopoly.app

import de.vs.monopoly.logic.Global
import de.vs.monopoly.logic.Boards
import de.vs.monopoly.logic.Throw
import org.json4s._
import org.scalatra._
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.scalate.ScalateSupport
import play.api.libs.ws.WSResponse
import de.vs.monopoly.logic._
import de.alexholly.util.http.HttpSync
import org.scalatra.json.JacksonJsonSupport
import org.json4s._
import org.json4s.native.Serialization.{read, write}

class BoardServlet extends ScalatraServlet with ScalateSupport with JacksonJsonSupport {

  protected implicit val jsonFormats: Formats = DefaultFormats

  protected override def transformRequestBody(body: JValue): JValue = body.camelizeKeys

  val TIMEOUT = 5000

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
    var response: WSResponse = null
    if (Global.testMode) {
      response = HttpSync.get(Global.default_url + "/games/" + params("gameid") + "/players/turn", TIMEOUT)
    } else {
      response = HttpSync.get("http://localhost:4567" + "/games/" + params("gameid") + "/players/turn", TIMEOUT)
    }
    if (response.status == 200) {
      var currPlayerid = response.body
      val _throw = parse(request.body).extract[Throw]
      Boards.rolled(params("gameid"), params("playerid"), currPlayerid, _throw) match {
        case Some(boardstatus) =>
          updateBoard(params("gameid"), boardstatus)
          boardstatus
        case None =>
          //TODO quick and dirty, illegal roll update
          val board = BoardStatus(Boards.getPlayer(params("gameid"), currPlayerid).get, Boards.get(params("gameid")).get, Event())
          updateBoard(params("gameid"), board)
          board
          //NotFound()
      }
    } else {
      response.status
    }
  }

  def updateBoard(gameid: String, board: BoardStatus) {
    val message = Message("SERVER", "update_board", "EGAL", write(board))
    HttpSync.post("http://localhost:4567/messages/send/"+gameid, write(message), TIMEOUT)
  }
}