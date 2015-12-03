/**
 * Created by alex on 11.11.15.
 */

import de.alexholly.util.http.HttpSync._
import de.vs.monopoly._
import org.json4s.jackson.JsonMethods._
import org.json4s.DefaultFormats
import org.scalatest._

import scala.concurrent.duration._

//@TODO
/*
 * Alle tests müssen nach einer aktion nochmals mit einem get pruefen
 * ob die Werte wirklich gesetzt wurden, dies fehlt an vielen Stellen.
 * Testen des Status sagt nur teilweise ueber die rest Schnittstelle aus aber nicht viel ueber die Logik.
 */

class DiceTest extends FunSuite with BeforeAndAfter {

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
  var server = JettyServer().startOnFreePort()
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

    val obj = parse(response.body).extract[Roll]
    assert((1 to 6).contains(obj.number))
  }
}