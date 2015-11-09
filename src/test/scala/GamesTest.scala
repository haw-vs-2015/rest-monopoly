import org.scalatra.test.scalatest._
import org.scalatest.FunSuiteLike
import de.vs.rest.server.monopoly.app.MonopolyServlet

import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.scalatra.json.{ JValueResult, JacksonJsonSupport }

import de.vs.monopoly._

class GamesTest extends ScalatraSuite with FunSuiteLike {
  // `GamesTest` is your app which extends ScalatraServlet
  addServlet(classOf[MonopolyServlet], "/*")
  protected implicit val jsonFormats: Formats = DefaultFormats

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

  //ListBuffer klappt nicht, mit immutable collection loesen?
  test("get games players") {
    get("/games/1") {
      status should equal(200)

      parseOpt(body) match {
        case Some(json) => json.extract[Game] 
//        match {
//          case Some(game) => assert(game.gameid == 1)
//          case None => fail(body)
//        }
        case None => fail(body)
      }
    }
  }
  //Some missing

  //  test("post create a new game") {
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