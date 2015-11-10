import org.scalatra.test.scalatest._
import org.scalatest.FunSuiteLike
import de.vs.rest.server.monopoly.app.MonopolyServlet

import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.scalatra.json.{JValueResult, JacksonJsonSupport}

import de.vs.monopoly._

class GamesTest extends ScalatraSuite with FunSuiteLike {
  // `GamesTest` is your app which extends ScalatraServlet
  addServlet(classOf[MonopolyServlet], "/*")
  protected implicit val jsonFormats: Formats = DefaultFormats

  val BODY_MESSAGE = "Empty?"

  test("wuerfeln") {
    get("/dice") {
      status should equal(200)

      val obj = parse(body).extract[Roll]
      assert((1 to 6).contains(obj.number))
    }
  }

  test("get games") {
    get("/games") {
      status should equal(200)

      val obj = parse(body).extract[Games]
      assert(obj.games.isEmpty)
    }
  }

  test("post create a new game") {
    post("/games") {
      status should equal(201)
    }
  }

  test("returns the current game status") {
    get("/games/1") {
      status should equal(200)

      parseOpt(body) match {
        case Some(json) => json.extractOpt[Game]
        match {
          case Some(game) => assert(game.gameid == "1")
          case None => fail(body)
        }
        case None => fail(body + BODY_MESSAGE)
      }
    }
  }

  test("returns all joined players") {
    get("/games/1/players") {
      status should equal(200)
      parseOpt(body) match {
        case Some(json) => json.extractOpt[List[Player]]
        match {
          case Some(lst) => assert(lst.length == 0)
          case None => fail(body)
        }
        case None => fail(body + BODY_MESSAGE)

      }
    }
  }

  test("Gets a {gameid}players") {
    get("/games/1/players/1") {
      status should equal(200)
      parseOpt(body) match {
        case Some(json) => json.extractOpt[Player]
        match {
          case Some(player) => fail("Player should be Empty")
          case None => fail(body)
        }
        case None => assert(body == "")
      }
    }
  }

  test("joins the player to the game ") {
    val name = "Mustermann"
    val uri = "http://localhost:4567/player/" + name.toLowerCase()

    //put sollte id liefern
    put("/games/1/players", Map("name" -> name), Map("uri" -> uri)) {}


    get("/games/1/players/" + name.toLowerCase) {
      status should equal(200)
      parseOpt(body) match {
        case Some(json) => json.extractOpt[Player]
        match {
          case Some(player) =>
            assert(player.id == name.toLowerCase)
            assert(player.name == name)
            assert(player.uri == uri)
          case None => fail(body)
        }
        case None => fail(body + BODY_MESSAGE)
      }
    }
  }

  //
  //    get("/games") {
  //      status should equal(200)
  //
  //      val obj = parse(body).extract[Games]
  //      assert(obj.games.isEmpty)
  //    }
  //
  //    post("/games") {
  //      status should equal(201)
  //    }
  //    
  //    get("/games/:1/players/:playerid") {
  //      status should equal(200)
  //
  //      val obj = parse(body).extract[Games]
  //      assert(obj.games.isEmpty)
  //    }
  //
  //  }
}