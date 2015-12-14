package de.vs

/**
 * Created by alex on 11.11.15.
 */

import de.alexholly.util.JettyServer
import de.alexholly.util.http.HttpSync._
import de.vs.monopoly.logic.{Global, Games, Roll, PlayerLocation, Boards}

import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.DefaultFormats
import org.scalatest._
import java.net.URLEncoder

//@TODO
/*
 * Alle tests müssen nach einer aktion nochmals mit einem get pruefen
 * ob die Werte wirklich gesetzt wurden, dies fehlt an vielen Stellen.
 * Testen des Status sagt nur teilweise ueber die rest Schnittstelle aus aber nicht viel ueber die Logik.
 */

class BoardTest extends FunSuite with BeforeAndAfter {

  //JSON stuff
  implicit val jsonFormats = DefaultFormats

  //Debugging stuff
  val BODY_MESSAGE = " BODY EMPTY?"
  val JSON_MESSAGE = " JSON ERROR"
  val EMPTY_MESSAGE = " SHOULD BE EMPTY"
  val TIMEOUT = 10000

  var server = JettyServer().startOnFreePort()
  //@TODO remove global stuff and if's from logic
  //@TODO Add service Manager and ask the ip/port
  Global.default_url = "http://localhost:" + server.port
  Global.testMode = true
  var default_url = Global.default_url

  after {
    Boards.reset()
    Games.reset()
  }

  test("wuerfeln") {
    var response = get(default_url + "/dice", TIMEOUT)
    assert(response.status == 200)

    var obj = parse(response.body).extract[Roll]
    assert((1 to 6).contains(obj.number))
  }

  test("get all boards") {
    var response = get(default_url + "/boards", TIMEOUT)
    assert(response.status == 200)

    var obj = parse(response.body).extract[Boards]
    assert(obj.boards.isEmpty)
  }

  test("find board - fail") {
    //find board
    var response = get(default_url + "/boards/1", TIMEOUT)
    assert(response.status == 404)
  }

  test("find board - success") {

    //create board 1
    var response = put(default_url + "/boards/1", TIMEOUT)
    assert(response.status == 201)

    //find board 1
    response = get(default_url + "/boards/1", TIMEOUT)
    assert(response.status == 200)

    response = get(default_url + "/boards", TIMEOUT)
    assert(response.status == 200)

    //check if board 1 is still available
    var obj = parse(response.body).extract[Boards]
    assert(obj.boards.size == 1)
  }

  test("board, gameid duplicate - fail conflict") {

    //create board
    var response = put(default_url + "/boards/1", TIMEOUT)
    assert(response.status == 201)

    //create same board again
    response = put(default_url + "/boards/1", TIMEOUT)
    assert(response.status == 409)
  }

  test("delete gameid(board) - success") {

    //create board 1
    var response = put(default_url + "/boards/1", TIMEOUT)
    assert(response.status == 201)

    //delete board 1
    response = delete(default_url + "/boards/1", TIMEOUT)
    assert(response.status == 200)

    //check if board 1 is deleted
    response = get(default_url + "/boards/1", TIMEOUT)
    assert(response.status == 404)
  }

  test("delete gameid(board) - fail") {

    //create board 1
    var response = put(default_url + "/boards/1", TIMEOUT)
    assert(response.status == 201)

    //try to delete non available board 2
    response = delete(default_url + "/boards/2", TIMEOUT)
    assert(response.status == 404)

    //check if board 1 is still there
    response = get(default_url + "/boards/1", TIMEOUT)
    assert(response.status == 200)
  }

  test("get all players on board empty - success") {

    //create board 1
    var response = put(default_url + "/boards/1", TIMEOUT)
    assert(response.status == 201)

    //get all players on board
    response = get(default_url + "/boards/1/players", TIMEOUT)
    assert(response.status == 200)

    //check if list it empty
    var obj = parse(response.body).extract[List[PlayerLocation]]
    assert(obj.isEmpty)
  }

  test("put/get/delete player on board - success") {

    //create board 1
    var response = put(default_url + "/boards/1", TIMEOUT)
    assert(response.status == 201)

    //put player on board
    response = put(default_url + "/boards/1/players/1", TIMEOUT)
    assert(response.status == 201)

    //check if player is on board
    response = get(default_url + "/boards/1/players/1", TIMEOUT)
    assert(response.status == 200)

    //get all players on board
    response = get(default_url + "/boards/1/players", TIMEOUT)
    assert(response.status == 200)

    //check if list contains one player
    var obj = parse(response.body).extract[List[PlayerLocation]]
    assert(obj.size == 1)

    //remove players from board
    response = delete(default_url + "/boards/1/players/1", TIMEOUT)
    assert(response.status == 200)

    //get all players on board
    response = get(default_url + "/boards/1/players", TIMEOUT)
    assert(response.status == 200)

    //check if list is emtpy
    obj = parse(response.body).extract[List[PlayerLocation]]
    assert(obj.isEmpty)
  }

  test("Würfeln und Spieler wechsel") {
    var name1 = "Mustermann1"
    var name2 = "Mustermann2"
    var name3 = "Mustermann3"
    var name4 = "Mustermann4"
    var uri1 = "http://localhost:" + server.port
    var uri2 = "http://localhost:" + server.port
    var uri3 = "http://localhost:" + server.port
    var uri4 = "http://localhost:" + server.port
    var uri1_encoded = URLEncoder.encode(uri1, "UTF-8")
    var uri2_encoded = URLEncoder.encode(uri2, "UTF-8")
    var uri3_encoded = URLEncoder.encode(uri3, "UTF-8")
    var uri4_encoded = URLEncoder.encode(uri4, "UTF-8")

    //Create a game
    post(default_url + "/games", TIMEOUT)

    //Create a player
    createPlayer(name1, uri1_encoded)
    createPlayer(name2, uri2_encoded)
    createPlayer(name3, uri3_encoded)
    createPlayer(name4, uri4_encoded)

    //make player1 ready
    var response = put(default_url + "/games/1/players/" + 1 + "/ready", TIMEOUT)
    assert(response.status == 200)

    //make player2 ready
    response = put(default_url + "/games/1/players/" + 2 + "/ready", TIMEOUT)
    assert(response.status == 200)

    //make player3 ready
    response = put(default_url + "/games/1/players/" + 3 + "/ready", TIMEOUT)
    assert(response.status == 200)

    //make player4 ready
    response = put(default_url + "/games/1/players/" + 4 + "/ready", TIMEOUT)
    assert(response.status == 200)

    //start game
    response = put(default_url + "/games/1/start", TIMEOUT)
    assert(response.status == 200)

    //@TODO Player fehlt, roll erwartet Post objekt als json und nicht nur Throw
    var _throw = "{" +
      "\"roll1\": {\"number\":21 }," +
      "\"roll2\": {\"number\":42 } " +
      " }"

    response = post(default_url + "/boards/1/players/" + 4 + "/roll", _throw, TIMEOUT)
    assert(response.status == 200)

    //@TODO Test ob der Spieler korrekt auf dem board bewegt wurde
  }

  //@TODO Ich glaub es fehlen noch paar tests
  def createPlayer(name: String, uri_encoded: String) = {
    var response = put(Global.default_url + "/games/1/players/" + name + "?name=" + name + "&uri=" + uri_encoded, TIMEOUT)
    assert(response.status == 200)
  }
}