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

  //@TODO Check if Port already used in jetty server?
  //@TODO error messages port already in use
  //Jetty server(restart) stuff
  var server: JettyServer = JettyServer().start()
  default_url = "http://localhost:" + server.port

  after {
    Boards.reset()
    Games.reset()
  }

  test("wuerfeln") {
    var response = get("/dice", TIMEOUT)
    assert(response.status == 200)

    val obj = parse(response.body).extract[Roll]
    assert((1 to 6).contains(obj.number))
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
    var response = put("/games/1/players/" + name + "?name=" + name + "&uri=" + uri_encoded, TIMEOUT)
    assert(response.status == 200)
  }
}