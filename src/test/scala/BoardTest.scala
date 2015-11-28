/**
 * Created by alex on 11.11.15.
 */

import de.vs.http.client.Http._
import de.vs.monopoly._
import org.json4s.jackson.JsonMethods._
import org.json4s.{DefaultFormats, _}
import org.scalatest._

import scala.concurrent.duration._

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
  val TIMEOUT = 10 seconds

  var server = JettyServer().startOnFreePort()
  Global.default_url = "http://localhost:" + server.port
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
    response = put(default_url + "/boards/1/players/mustermann", TIMEOUT)
    assert(response.status == 201)

    //check if player is on board
    response = get(default_url + "/boards/1/players/mustermann", TIMEOUT)
    assert(response.status == 200)

    //get all players on board
    response = get(default_url + "/boards/1/players", TIMEOUT)
    assert(response.status == 200)

    //check if list contains one player
    var obj = parse(response.body).extract[List[PlayerLocation]]
    assert(obj.size == 1)

    //remove players from board
    response = delete(default_url + "/boards/1/players/mustermann", TIMEOUT)
    assert(response.status == 200)

    //get all players on board
    response = get(default_url + "/boards/1/players", TIMEOUT)
    assert(response.status == 200)

    //check if list is emtpy
    obj = parse(response.body).extract[List[PlayerLocation]]
    assert(obj.isEmpty)
  }

  //@TODO Ich glaub es fehlen noch paar tests

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
}