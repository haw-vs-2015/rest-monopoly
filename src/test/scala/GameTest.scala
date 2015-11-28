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
import play.api.libs.ws.WSResponse
import scala.concurrent.duration._
import de.vs.http.client.Http._

//@TODO
/*
 * Alle tests müssen nach einer aktion nochmals mit einem get pruefen
 * ob die Werte wirklich gesetzt wurden, dies fehlt an vielen Stellen.
 * Testen des Status sagt nur teilweise ueber die rest Schnittstelle aus aber nicht viel ueber die Logik.
 */

class GameTest extends FunSuite with BeforeAndAfter {

  //JSON stuff
  implicit val jsonFormats = DefaultFormats

  //Debugging stuff
  val BODY_MESSAGE = " BODY EMPTY?"
  val JSON_MESSAGE = " JSON ERROR"
  val EMPTY_MESSAGE = " SHOULD BE EMPTY"
  val TIMEOUT = 10 seconds

  var server = JettyServer().startOnFreePort()
  Global.default_url = "http://localhost:" + server.port
  var default_url = Global.default_url

  after {
    Boards.reset()
    Games.reset()
  }

  test("get games") {
    val response: WSResponse = get(default_url + "/games", TIMEOUT)
    assert(response.status == 200)

    var obj = parse(response.body).extract[Games]
    assert(obj.games.isEmpty)
  }

  test("post create a new game") {
    var response = post(default_url + "/games", TIMEOUT)
    assert(response.status == 201)

    response = get(default_url + "/games", TIMEOUT)
    assert(response.status == 200)

    var obj = parse(response.body).extract[Games]

    assert(!obj.games.isEmpty)

    //check board created
    response = get(default_url + "/boards", TIMEOUT)
    assert(response.status == 200)

    var obj2 = parse(response.body).extract[Boards]

    assert(obj2.boards.size == 1)
  }

  test("returns the current game status - no game available - negativ") {
    var response = get(default_url + "/games/1", TIMEOUT)
    assert(response.status == 404)
  }

  test("returns the current game status - game 1 available - positiv") {
    var response = post(default_url + "/games", TIMEOUT)
    assert(response.status == 201)

    response = get(default_url + "/games/1", TIMEOUT)
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
    var response = post(default_url + "/games", TIMEOUT)
    assert(response.status == 201)

    response = get(default_url + "/games/1/players", TIMEOUT)
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
    var response = get(default_url + "/games/1/players/1", TIMEOUT)
    assert(response.status == 404)
  }

  test("joins a player to the game with name and uri") {
    var name = "Mustermann"
    var uri = "http://localhost:4567"
    var uri_encoded = URLEncoder.encode(uri, "UTF-8")

    //Create a game
    var response = post(default_url + "/games", TIMEOUT)
    assert(response.status == 201)

    //@TODO game anfragen pruefen ob vorhanden
    response = get(default_url + "/games/1", TIMEOUT)
    assert(response.status == 200)

    //@TODO put sollte id liefern
    createPlayer(name, uri_encoded)

    response = get(default_url + "/games/1/players/" + name.toLowerCase, TIMEOUT)
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

    response = get(default_url + "/boards/1/players/" + name.toLowerCase, TIMEOUT)
    assert(response.status == 200)

    var obj2 = parse(response.body).extract[PlayerLocation]
    assert(obj2.id == name.toLowerCase)
  }

  test("delete a player") {
    var name = "Mustermann"
    var uri = "http://localhost:4567"
    var uri_encoded = URLEncoder.encode(uri, "UTF-8")

    //Create a game
    var response = post(default_url + "/games", TIMEOUT)
    assert(response.status == 201)

    //@TODO put sollte id liefern spieler erstellen
    createPlayer(name, uri_encoded)

    //Check player created
    response = get(default_url + "/games/1/players/" + name.toLowerCase, TIMEOUT)
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

    response = delete(default_url + "/games/1/players/" + name.toLowerCase, TIMEOUT)
    assert(response.status == 200)

    //Check player deleted
    response = get(default_url + "/games/1/players/" + name.toLowerCase, TIMEOUT)
    assert(response.status == 404)

  }

  test("get ready status tells if the player is ready to start the game ") {
    var name = "Mustermann"

    //Check ready
    var response = get(default_url + "/games/1/players/" + name.toLowerCase + "/" + "ready", TIMEOUT)
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
    var name = "Mustermann"
    var uri = "http://localhost:4567"
    var uri_encoded = URLEncoder.encode(uri, "UTF-8")

    //Create a game
    var response = post(default_url + "/games", TIMEOUT)
    assert(response.status == 201)

    //put sollte id liefern
    createPlayer(name, uri_encoded)

    //check not ready
    response = get(default_url + "/games/1/players/" + name.toLowerCase + "/" + "ready", TIMEOUT)
    assert(response.status == 200)

    parseOpt(response.body) match {
      case Some(json) => json.extractOpt[Boolean] match {
        case Some(bool) => assert(bool == false)
        case None => fail(json + JSON_MESSAGE)
      }
      case None => fail(response.body + BODY_MESSAGE)
    }

    //set ready
    response = put(default_url + "/games/1/players/" + name.toLowerCase + "/" + "ready", TIMEOUT)
    assert(response.status == 200)

    //check is ready
    response = get(default_url + "/games/1/players/" + name.toLowerCase + "/" + "ready", TIMEOUT)
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
    var name = "Mustermann"
    var uri = "http://localhost:4567"
    var uri_encoded = URLEncoder.encode(uri, "UTF-8")

    //Create a game
    var response = post(default_url + "/games", TIMEOUT)
    assert(response.status == 201)

    //Create a player
    createPlayer(name, uri_encoded)

    response = get(default_url + "/games/1/players/current", TIMEOUT)
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
    var name = "Mustermann"
    var uri = "http://localhost:4567"
    var uri_encoded = URLEncoder.encode(uri, "UTF-8")

    //Create a game
    var response = post(default_url + "/games", TIMEOUT)
    assert(response.status == 201)

    //Create a player
    createPlayer(name, uri_encoded)

    response = get(default_url + "/games/1/players/turn", TIMEOUT)
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
    var name = "Mustermann"
    var uri = "http://localhost:4567"
    var uri_encoded = URLEncoder.encode(uri, "UTF-8")
    var name2 = "noplayer"
    var uri2 = "http://localhost:4567"
    var uri2_encoded = URLEncoder.encode(uri2, "UTF-8")

    //Create a game
    var response = post(default_url + "/games", TIMEOUT)
    assert(response.status == 201)

    //Create a player
    createPlayer(name, uri_encoded)
    createPlayer(name2, uri2_encoded)

    //Try get mutex
    response = put(default_url + "/games/1/players/" + name.toLowerCase + "/turn", TIMEOUT)
    assert(response.status == 201)

    //Try get mutex
    response = put(default_url + "/games/1/players/" + name.toLowerCase + "/turn", TIMEOUT)
    assert(response.status == 200)

    //Try get mutex
    response = put(default_url + "/games/1/players/" + name2.toLowerCase + "/turn", TIMEOUT)
    assert(response.status == 409)
  }

  test("gets the player holding the turn mutex2") {
    var name = "Mustermann"
    var uri = "http://localhost:4567"
    var uri_encoded = URLEncoder.encode(uri, "UTF-8")

    //Create a game
    var response = post(default_url + "/games", TIMEOUT)
    assert(response.status == 201)

    //Create a player
    createPlayer(name, uri_encoded)

    //Try get mutex
    response = put(default_url + "/games/1/players/" + name.toLowerCase + "/turn", TIMEOUT)
    assert(response.status == 201)

    //get mutex
    response = get(default_url + "/games/1/players/turn", TIMEOUT)
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
    var name = "Mustermann"
    var uri = "http://localhost:4567"
    var uri_encoded = URLEncoder.encode(uri, "UTF-8")

    //Create a game
    var response = post(default_url + "/games", TIMEOUT)
    assert(response.status == 201)

    //Create a player
    createPlayer(name, uri_encoded)

    response = delete(default_url + "/games/1/players/turn", TIMEOUT)
    assert(response.status == 200)

    response = get(default_url + "/games/1/players/turn", TIMEOUT)
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
    var name1 = "Mustermann1"
    var name2 = "Mustermann2"
    var name3 = "Mustermann3"
    var name4 = "Mustermann4"
    var uri1 = "http://localhost:4567"
    var uri2 = "http://localhost:4567"
    var uri3 = "http://localhost:4567"
    var uri4 = "http://localhost:4567"
    var uri1_encoded = URLEncoder.encode(uri1, "UTF-8")
    var uri2_encoded = URLEncoder.encode(uri2, "UTF-8")
    var uri3_encoded = URLEncoder.encode(uri3, "UTF-8")
    var uri4_encoded = URLEncoder.encode(uri4, "UTF-8")

    //Create a game
    var response = post(default_url + "/games", TIMEOUT)
    assert(response.status == 201)

    //Create 4 players
    createPlayer(name1, uri1_encoded)
    createPlayer(name2, uri2_encoded)
    createPlayer(name3, uri3_encoded)
    createPlayer(name4, uri4_encoded)

    //Check game not started
    response = get(default_url + "/games/1", TIMEOUT)
    assert(response.status == 200)

    parseOpt(response.body) match {
      case Some(json) => json.extractOpt[Game] match {
        case Some(game) => assert(game.started == false)
        case None => fail(json + JSON_MESSAGE)
      }
      case None => assert(response.body == "")
    }

    //make player1 ready
    response = put(default_url + "/games/1/players/" + name1.toLowerCase + "/ready", TIMEOUT)
    assert(response.status == 200)

    //make player2 ready
    response = put(default_url + "/games/1/players/" + name2.toLowerCase + "/ready", TIMEOUT)
    assert(response.status == 200)

    //make player3 ready
    response = put(default_url + "/games/1/players/" + name3.toLowerCase + "/ready", TIMEOUT)
    assert(response.status == 200)

    //Check again game not started
    response = get(default_url + "/games/1", TIMEOUT)
    assert(response.status == 200)

    parseOpt(response.body) match {
      case Some(json) => json.extractOpt[Game] match {
        case Some(game) => assert(game.started == false)
        case None => fail(json + JSON_MESSAGE)
      }
      case None => assert(response.body == "")
    }

    //make player4 ready
    response = put(default_url + "/games/1/players/" + name4.toLowerCase + "/ready", TIMEOUT)
    assert(response.status == 200)

    //start game
    response = put(default_url + "/games/1/start", TIMEOUT)
    assert(response.status == 200)

    //Check game started
    response = get(default_url + "/games/1", TIMEOUT)
    assert(response.status == 200)

    parseOpt(response.body) match {
      case Some(json) => json.extractOpt[Game] match {
        case Some(game) => assert(game.started == true)
        case None => fail(json + JSON_MESSAGE)
      }
      case None => assert(response.body == "")
    }
  }

  test("remove game if all players left") {
    var name1 = "Mustermann1"
    var uri1 = "http://localhost:4567/player/" + name1.toLowerCase()
    var uri1_encoded = URLEncoder.encode(uri1, "UTF-8")

    //Create a game
    var response = post(default_url + "/games", TIMEOUT)
    assert(response.status == 201)

    //check game created
    response = get(default_url + "/games", TIMEOUT)
    assert(response.status == 200)

    var obj = parse(response.body).extract[Games]
    assert(obj.games.size == 1)

    //Create a player
    //player joins game 1
    createPlayer(name1, uri1_encoded)

    //check game still there
    response = get(default_url + "/games", TIMEOUT)
    assert(response.status == 200)

    obj = parse(response.body).extract[Games]
    assert(obj.games.size == 1)

    //delete player (leave game)
    response = delete(default_url + "/games/1/players/" + name1.toLowerCase, TIMEOUT)
    assert(response.status == 200)

    //Check player deleted
    response = get(default_url + "/games/1/players/" + name1.toLowerCase, TIMEOUT)
    assert(response.status == 404)

    //check game removed
    response = get(default_url + "/games", TIMEOUT)
    assert(response.status == 200)

    obj = parse(response.body).extract[Games]
    assert(obj.games.isEmpty)

  }

  //  test("Würfeln und Spieler wechsel") {
  //    val name = "Mustermann"
  //    val uri = "http://localhost:4567/player/" + name.toLowerCase()
  //    val uri_encoded = URLEncoder.encode(uri, "UTF-8")
  //
  //    //Create a game
  //    post("/games", TIMEOUT)
  //
  //    //Create a player
  //    put("/games/1/players/" + name + "/" + uri_encoded, TIMEOUT)
  //
  //    //@TODO Player fehlt, roll erwartet Post objekt als json und nicht nur Throw
  //    var _throw = "{" +
  //      "\"roll1\": {\"number\":21 }," +
  //      "\"roll2\": {\"number\":42 } " +
  //      " }"
  //
  //    var response = post("/boards/1/players/" + name.toLowerCase + "/roll", _throw, TIMEOUT)
  //    assert(response.status == 200)
  //
  //  }

  //@TODO initGame methode x players - refactoring

  def createPlayer(name: String, uri_encoded: String) = {
    var response = put(Global.default_url + "/games/1/players/" + name + "?name=" + name + "&uri=" + uri_encoded, TIMEOUT)
    assert(response.status == 200)
  }
}