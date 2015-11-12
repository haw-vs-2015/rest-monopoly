/**
 * Created by alex on 11.11.15.
 */

import java.net.URLEncoder
import de.vs.monopoly._
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.DefaultFormats
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.scalatest._
import scala.concurrent.duration._
import de.vs.http.client.Http._

//@TODO
/*
 * Alle tests müssen nach einer aktion nochmals mit einem get pruefen
 * ob die Werte wirklich gesetzt wurden, dies fehlt an vielen Stellen.
 * Testen des Status sagt nur teilweise ueber die rest Schnittstelle aus aber nicht viel ueber die Logik.
 */

class GamesTest2 extends FunSuite with BeforeAndAfter {

  //JSON stuff
  implicit val jsonFormats = DefaultFormats

  //Debugging stuff
  val BODY_MESSAGE = " BODY EMPTY?"
  val JSON_MESSAGE = " JSON ERROR"
  val EMPTY_MESSAGE = " SHOULD BE EMPTY"
  val TIMEOUT = 10 seconds

  //Jetty server(restart) stuff
  var server: JettyServer = JettyServer().start()

  after {
    Boards.resetBoards()
    Games.resetGames()
  }

  test("wuerfeln") {
    var response = get("/dice", TIMEOUT)
    assert(response.status == 200)

    val obj = parse(response.body).extract[Roll]
    assert((1 to 6).contains(obj.number))
  }

  test("get games") {
    var response = get("/games", TIMEOUT)
    assert(response.status == 200)

    val obj = parse(response.body).extract[Games]
    assert(obj.games.isEmpty)
  }

  test("post create a new game") {
    var response = post("/games", TIMEOUT)
    assert(response.status == 201)

    response = get("/games", TIMEOUT)
    assert(response.status == 200)

    val obj = parse(response.body).extract[Games]

    assert(!obj.games.isEmpty)
  }

  test("returns the current game status - no game available - negativ") {
    var response = get("/games/1", TIMEOUT)
    assert(response.status == 200)

    parseOpt(response.body) match {
      case Some(json) =>
        json.extractOpt[Game] match {
          case Some(game) => fail(response.body + EMPTY_MESSAGE)
          case None => assert(response.body == "")
        }
      case None => assert(response.body == "")
    }
  }

  test("returns the current game status - game 1 available - positiv") {
    var response = post("/games", TIMEOUT)
    assert(response.status == 201)

    response = get("/games/1", TIMEOUT)
    assert(response.status == 200)

    parseOpt(response.body) match {
      case Some(json) =>
        json.extractOpt[Game] match {
          case Some(game) => assert(game.gameid == "1")
          case None => fail(response.body + JSON_MESSAGE)
        }
      case None => fail(response.body + BODY_MESSAGE)
    }
  }

  test("returns all joined players - positiv") {
    var response = post("/games", TIMEOUT)
    assert(response.status == 201)

    response = de.vs.http.client.Http.get("/games/1/players", TIMEOUT)
    assert(response.status == 200)

    parseOpt(response.body) match {
      case Some(json) => json.extractOpt[List[Player]] match {
        case Some(lst) => assert(lst.length == 0)
        case None => fail(response.body + JSON_MESSAGE)
      }
      case None => fail(response.body + BODY_MESSAGE)
    }
  }

  test("Gets a player with game id and players id - negativ") {
    var response = get("/games/1/players/1", TIMEOUT)
    assert(response.status == 200)

    parseOpt(response.body) match {
      case Some(json) => json.extractOpt[Player]
      match {
        case Some(player) => fail("Player should be Empty")
        case None => fail(response.body + JSON_MESSAGE)
      }
      case None => assert(response.body == "")
    }
  }


  test("joins a player to the game with name and uri") {
    val name = "Mustermann"
    val uri = "http://localhost:4567/player/" + name.toLowerCase()
    val uri_encoded = URLEncoder.encode(uri, "UTF-8")

    //Create a game
    post("/games", TIMEOUT)
    var response = get("/games/1", TIMEOUT)
    assert(response.status == 200)

    //put sollte id liefern
    response = put("/games/1/players/" + name + "/" + uri_encoded, TIMEOUT)
    assert(response.status == 200)

    response = get("/games/1/players/" + name.toLowerCase, TIMEOUT)
    assert(response.status == 200)
    parseOpt(response.body) match {
      case Some(json) => json.extractOpt[Player] match {
        case Some(player) =>
          assert(player.id == name.toLowerCase)
          assert(player.name == name)
          assert(player.uri == uri)
        case None => fail(json + JSON_MESSAGE)
      }
      case None => fail(response.body + BODY_MESSAGE)
    }
  }

  test("delete a player") {
    val name = "Mustermann"
    val uri = "http://localhost:4567/player/" + name.toLowerCase()
    val uri_encoded = URLEncoder.encode(uri, "UTF-8")

    //Create a game
    post("/games", TIMEOUT)

    //put sollte id liefern spieler erstellen
    var response = put("/games/1/players/" + name + "/" + uri_encoded, TIMEOUT)
    assert(response.status == 200)

    //Check player created
    response = get("/games/1/players/" + name.toLowerCase, TIMEOUT)
    assert(response.status == 200)
    parseOpt(response.body) match {
      case Some(json) => json.extractOpt[Player] match {
        case Some(player) =>
          assert(player.id == name.toLowerCase)
          assert(player.name == name)
          assert(player.uri == uri)
        case None => fail(json + JSON_MESSAGE)
      }
      case None => fail(response.body + BODY_MESSAGE)
    }

    response = delete("/games/1/players/" + name.toLowerCase, TIMEOUT)
    assert(response.status == 200)

    //Check player deleted
    response = get("/games/1/players/" + name.toLowerCase, TIMEOUT)
    assert(response.status == 200)
    parseOpt(response.body) match {
      case Some(json) => json.extractOpt[Player] match {
        case Some(player) => fail(json + EMPTY_MESSAGE)
        case None =>
      }
      case None => assert(response.body == "")
    }
  }

  test("get ready status tells if the player is ready to start the game ") {
    val name = "Mustermann"

    var response = get("/games/1/players/" + name.toLowerCase + "/" + "ready", TIMEOUT)
    assert(response.status == 200)
    parseOpt(response.body) match {
      case Some(json) => json.extractOpt[Boolean] match {
        case Some(bool) => assert(bool == false)
        case None => fail(json + JSON_MESSAGE)
      }
      case None => fail(response.body + BODY_MESSAGE)
    }
  }

  test("signals that the player is ready to start the game / is finished with his turn") {
    val name = "Mustermann"
    val uri = "http://localhost:4567/player/" + name.toLowerCase()
    val uri_encoded = URLEncoder.encode(uri, "UTF-8")

    //Create a game
    post("/games", TIMEOUT)

    //put sollte id liefern
    var response = put("/games/1/players/" + name + "/" + uri_encoded, TIMEOUT)
    assert(response.status == 200)

    //check not ready
    response = get("/games/1/players/" + name.toLowerCase + "/" + "ready", TIMEOUT)
    assert(response.status == 200)
    parseOpt(response.body) match {
      case Some(json) => json.extractOpt[Boolean] match {
        case Some(bool) => assert(bool == false)
        case None => fail(json + JSON_MESSAGE)
      }
      case None => fail(response.body + BODY_MESSAGE)
    }

    //set ready
    response = put("/games/1/players/" + name.toLowerCase + "/" + "ready", TIMEOUT)
    assert(response.status == 200)

    //check is ready
    response = get("/games/1/players/" + name.toLowerCase + "/" + "ready", TIMEOUT)
    assert(response.status == 200)
    parseOpt(response.body) match {
      case Some(json) => json.extractOpt[Boolean] match {
        case Some(bool) => assert(bool == true)
        case None => fail(json + JSON_MESSAGE)
      }
      case None => fail(response.body + BODY_MESSAGE)
    }
  }

  test("gets the currently active player that shall take action") {
    val name = "Mustermann"
    val uri = "http://localhost:4567/player/" + name.toLowerCase()
    val uri_encoded = URLEncoder.encode(uri, "UTF-8")

    //Create a game
    post("/games", TIMEOUT)

    //Create a player
    var response = put("/games/1/players/" + name + "/" + uri_encoded, TIMEOUT)

    response = get("/games/1/players/current", TIMEOUT)
    assert(response.status == 200)
    parseOpt(response.body) match {
      case Some(json) => json.extractOpt[Player] match {
        case Some(player) => assert(player.id == name.toLowerCase)
        case None => fail(json + JSON_MESSAGE)
      }
      case None => fail(response.body + BODY_MESSAGE)
    }
  }

  test("gets the player holding the turn mutex") {
    val name = "Mustermann"
    val uri = "http://localhost:4567/player/" + name.toLowerCase()
    val uri_encoded = URLEncoder.encode(uri, "UTF-8")

    //Create a game
    post("/games", TIMEOUT)

    //Create a player
    put("/games/1/players/" + name + "/" + uri_encoded, TIMEOUT)

    var response = get("/games/1/players/turn", TIMEOUT)
    assert(response.status == 200)
    parseOpt(response.body) match {
      case Some(json) => json.extractOpt[String] match {
        case Some(playerid) => fail("Error, keiner hat den Mutex " + json)
        case None => fail(json + JSON_MESSAGE)
      }
      case None => assert(response.body == "")
    }
  }

  test("tries to aquire the turn mutex") {
    val name = "Mustermann"
    val uri = "http://localhost:4567/player/" + name.toLowerCase()
    val uri_encoded = URLEncoder.encode(uri, "UTF-8")
    val name2 = "noplayer"

    //Create a game
    post("/games", TIMEOUT)

    //Create a player
    put("/games/1/players/" + name + "/" + uri_encoded, TIMEOUT)
    put("/games/1/players/" + name2 + "/" + uri_encoded, TIMEOUT)

    var response = put("/games/1/players/" + name.toLowerCase + "/turn", TIMEOUT)
    assert(response.status == 201)

    response = put("/games/1/players/" + name.toLowerCase + "/turn", TIMEOUT)
    assert(response.status == 200)

    response = put("/games/1/players/" + name2.toLowerCase + "/turn", TIMEOUT)
    assert(response.status == 409)
  }

  test("gets the player holding the turn mutex2") {
    val name = "Mustermann"
    val uri = "http://localhost:4567/player/" + name.toLowerCase()
    val uri_encoded = URLEncoder.encode(uri, "UTF-8")

    //Create a game
    post("/games", TIMEOUT)

    //Create a player
    put("/games/1/players/" + name + "/" + uri_encoded, TIMEOUT)

    var response = put("/games/1/players/" + name.toLowerCase + "/turn", TIMEOUT)
    assert(response.status == 201)

    response = get("/games/1/players/turn", TIMEOUT)
    assert(response.status == 200)

    parseOpt("\"" + response.body + "\"") match {
      case Some(json) => json.extractOpt[String] match {
        case Some(playerid) => assert(playerid == name.toLowerCase)
        case None => fail(json + JSON_MESSAGE)
      }
      case None => fail(response.body + BODY_MESSAGE)
    }
  }

  test("releases the mutex") {
    val name = "Mustermann"
    val uri = "http://localhost:4567/player/" + name.toLowerCase()
    val uri_encoded = URLEncoder.encode(uri, "UTF-8")

    //Create a game
    post("/games", TIMEOUT)

    //Create a player
    put("/games/1/players/" + name + "/" + uri_encoded, TIMEOUT)

    var response = delete("/games/1/players/turn", TIMEOUT)
    assert(response.status == 200)

    response = get("/games/1/players/turn", TIMEOUT)
    assert(response.status == 200)
    parseOpt(response.body) match {
      case Some(json) => json.extractOpt[String] match {
        case Some(playerid) => fail("Error, keiner hat den Mutex " + json)
        case None => fail(json + JSON_MESSAGE)
      }
      case None => assert(response.body == "")
    }
  }

  test("set player ready on lobby start game") {
    val name1 = "Mustermann1"
    val name2 = "Mustermann2"
    val name3 = "Mustermann3"
    val name4 = "Mustermann4"
    val uri1 = "http://localhost:4567/player/" + name1.toLowerCase()
    val uri2 = "http://localhost:4567/player/" + name2.toLowerCase()
    val uri3 = "http://localhost:4567/player/" + name3.toLowerCase()
    val uri4 = "http://localhost:4567/player/" + name4.toLowerCase()
    val uri1_encoded = URLEncoder.encode(uri1, "UTF-8")
    val uri2_encoded = URLEncoder.encode(uri2, "UTF-8")
    val uri3_encoded = URLEncoder.encode(uri3, "UTF-8")
    val uri4_encoded = URLEncoder.encode(uri4, "UTF-8")

    //Create a game
    post("/games", TIMEOUT)

    //Create 4 players
    put("/games/1/players/" + name1 + "/" + uri1_encoded, TIMEOUT)
    put("/games/1/players/" + name2 + "/" + uri2_encoded, TIMEOUT)
    put("/games/1/players/" + name3 + "/" + uri3_encoded, TIMEOUT)
    put("/games/1/players/" + name4 + "/" + uri4_encoded, TIMEOUT)

    //Check game not started
    var response = get("/games/1", TIMEOUT)
    assert(response.status == 200)
    parseOpt(response.body) match {
      case Some(json) => json.extractOpt[Game] match {
        case Some(game) => assert(game.started == false)
        case None => fail(json + JSON_MESSAGE)
      }
      case None => assert(response.body == "")
    }

    //make player1 ready
    response = put("/games/1/players/" + name1.toLowerCase + "/" + "ready", TIMEOUT)
    assert(response.status == 200)

    //make player2 ready
    response = put("/games/1/players/" + name2.toLowerCase + "/" + "ready", TIMEOUT)
    assert(response.status == 200)

    //make player3 ready
    response = put("/games/1/players/" + name3.toLowerCase + "/" + "ready", TIMEOUT)
    assert(response.status == 200)

    //Check again game not started
    response = get("/games/1", TIMEOUT)
    assert(response.status == 200)
    parseOpt(response.body) match {
      case Some(json) => json.extractOpt[Game] match {
        case Some(game) => assert(game.started == false)
        case None => fail(json + JSON_MESSAGE)
      }
      case None => assert(response.body == "")
    }

    //make player4 ready
    response = put("/games/1/players/" + name4.toLowerCase + "/" + "ready", TIMEOUT)
    assert(response.status == 200)

    //Check game started
    response = get("/games/1", TIMEOUT)
    assert(response.status == 200)
    parseOpt(response.body) match {
      case Some(json) => json.extractOpt[Game] match {
        case Some(game) => assert(game.started == true)
        case None => fail(json + JSON_MESSAGE)
      }
      case None => assert(response.body == "")
    }
  }

  test("Würfeln und Spieler wechsel") {
    val name = "Mustermann"
    val uri = "http://localhost:4567/player/" + name.toLowerCase()
    val uri_encoded = URLEncoder.encode(uri, "UTF-8")

    //Create a game
    post("/games", TIMEOUT)

    //Create a player
    put("/games/1/players/" + name + "/" + uri_encoded, TIMEOUT)

    //@TODO Player fehlt, roll erwartet Post objekt als json und nicht nur Throw
    var _throw = "{" +
      "\"roll1\": {\"number\":21 }," +
      "\"roll2\": {\"number\":42 } " +
      " }"

    var response = post("/boards/1/players/" + name.toLowerCase + "/roll", _throw, TIMEOUT)
    assert(response.status == 200)

  }

  //@TODO initGame methode x players - refactoring
}