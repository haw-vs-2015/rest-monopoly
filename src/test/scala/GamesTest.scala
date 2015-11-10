import org.scalatra.test.scalatest._
import org.scalatest.FunSuiteLike
import de.vs.rest.server.monopoly.app.MonopolyServlet

import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.scalatra.json.{JValueResult, JacksonJsonSupport}

import de.vs.monopoly._
import java.net.URLEncoder

class GamesTest extends ScalatraSuite with FunSuiteLike {
  // `GamesTest` is your app which extends ScalatraServlet
  addServlet(classOf[MonopolyServlet], "/*")
  implicit val jsonFormats = DefaultFormats

  val BODY_MESSAGE = " Empty?"
  val JSON_MESSAGE = " JSON ERROR"

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
          case None => fail(body + JSON_MESSAGE)
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
          case None => fail(body + JSON_MESSAGE)
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
          case None => fail(body + JSON_MESSAGE)
        }
        case None => assert(body == "")
      }
    }
  }

  test("joins the player to the game ") {
    val name = "Mustermann"
    val uri = "http://localhost:4567/player/" + name.toLowerCase()
    val uri_encoded = URLEncoder.encode(uri, "UTF-8")

    get("/games/1") {
      status should equal(200)
    }

    //put sollte id liefern
    put("/games/1/players/" + name + "/" + uri_encoded) {
      status should equal(200)
    }


    get("/games/1/players/" + name.toLowerCase) {
      status should equal(200)
      parseOpt(body) match {
        case Some(json) => json.extractOpt[Player]
        match {
          case Some(player) =>
            assert(player.id == name.toLowerCase)
            assert(player.name == name)
            assert(player.uri == uri)
          case None => fail(json + JSON_MESSAGE)
        }
        case None => fail(body + BODY_MESSAGE)
      }
    }
  }

  test("deletes a player") {
    val name = "Mustermann"
    delete("/games/1/players/" + name.toLowerCase) {
      status should equal(200)
    }
  }

  test("get ready status tells if the player is ready to start the game ") {

    val name = "Mustermann"

    get("/games/1/players/" + name.toLowerCase + "/" + "ready") {
      status should equal(200)
      parseOpt(body) match {
        case Some(json) => json.extractOpt[Boolean] match {
          case Some(bool) => assert(bool == false)
          case None => fail(json + JSON_MESSAGE)
        }
        case None => fail(body + BODY_MESSAGE)
      }
    }
  }

  test("signals that the player is ready to start the game / is finished with his turn") {
    val name = "Mustermann"
    val uri = "http://localhost:4567/player/" + name.toLowerCase()
    val uri_encoded = URLEncoder.encode(uri, "UTF-8")

    //put sollte id liefern
    put("/games/1/players/" + name + "/" + uri_encoded) {
      status should equal(200)
    }

    get("/games/1/players/" + name.toLowerCase + "/" + "ready") {
      status should equal(200)
      parseOpt(body) match {
        case Some(json) => json.extractOpt[Boolean] match {
          case Some(bool) => assert(bool == false)
          case None => fail(json + JSON_MESSAGE)
        }
        case None => fail(body + BODY_MESSAGE)
      }
    }

    put("/games/1/players/" + name.toLowerCase + "/" + "ready") {
      status should equal(200)
    }

    get("/games/1/players/" + name.toLowerCase + "/" + "ready") {
      status should equal(200)
      parseOpt(body) match {
        case Some(json) => json.extractOpt[Boolean] match {
          case Some(bool) => assert(bool == true)
          case None => fail(json + JSON_MESSAGE)
        }
        case None => fail(body + BODY_MESSAGE)
      }
    }
  }

  test("gets the currently active player that shall take action") {
    val name = "Mustermann"

    get("/games/1/players/current") {
      status should equal(200)
      parseOpt(body) match {
        case Some(json) => json.extractOpt[Player] match {
          case Some(player) => assert(player.id == name.toLowerCase)
          case None => fail(json + JSON_MESSAGE)
        }
        case None => fail(body + BODY_MESSAGE)
      }
    }
  }

  test("gets the player holding the turn mutex") {
    val name = "Mustermann"

    get("/games/1/players/turn") {
      status should equal(200)
      parseOpt(body) match {
        case Some(json) => json.extractOpt[String] match {
          case Some(playerid) =>
            fail("Error, keiner hat den Mutex " + json)
          case None => fail(json + JSON_MESSAGE)
        }
        case None => assert(body == "")
      }
    }
  }

  test("tries to aquire the turn mutex") {
    val name = "Mustermann"
    val name2 = "noplayer"

    put("/games/1/players/" + name.toLowerCase + "/turn") {
      status should equal(201)
    }

    put("/games/1/players/" + name.toLowerCase + "/turn") {
      status should equal(200)
    }

    put("/games/1/players/" + name2.toLowerCase + "/turn") {
      status should equal(409)
    }
  }

  test("gets the player holding the turn mutex2") {
    val name = "Mustermann"

    get("/games/1/players/turn") {
      status should equal(200)

      parseOpt("\"" + body + "\"") match {
        case Some(json) => json.extractOpt[String] match {
          case Some(playerid) => assert(playerid == name.toLowerCase)
          case None => fail(json + JSON_MESSAGE)
        }
        case None => fail(body + BODY_MESSAGE)
      }
    }
  }

  test("releases the mutex") {
    val name = "Mustermann"

    delete("/games/1/players/turn") {
      status should equal(200)
    }

    get("/games/1/players/turn") {
      status should equal(200)
      parseOpt(body) match {
        case Some(json) => json.extractOpt[String] match {
          case Some(playerid) =>
            fail("Error, keiner hat den Mutex " + json)
          case None => fail(json + JSON_MESSAGE)
        }
        case None => assert(body == "")
      }
    }
  }


  //BOARD

  test("get boards") {
    get("/boards") {
      status should equal(200)
      parseOpt(body) match {
        case Some(json) => json.extractOpt[List[String]] match {
          case Some(lst) => assert(lst.isEmpty)
          case None => fail(json + JSON_MESSAGE)
        }
        case None => assert(body == "")
      }
    }
  }
//
//  test("makes shure there is a board for the gameid") {
//    put("/boards") {
//      status should equal(200)
//      parseOpt(body) match {
//        case Some(json) => json.extractOpt[List[String]] match {
//          case Some(lst) => assert(lst.isEmpty)
//          case None => fail(json + JSON_MESSAGE)
//        }
//        case None => assert(body == "")
//      }
//    }
//  }

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