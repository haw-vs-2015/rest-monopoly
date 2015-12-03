///**
// * Created by alex on 11.11.15.
// */
//
//import de.alexholly.util.http.HttpSync._
//import de.vs.monopoly._
//import org.json4s.DefaultFormats
//import org.json4s.jackson.JsonMethods._
//import org.scalatest._
//import de.vs.rest.server.monopoly.app.Numbers
//import de.vs.rest.server.monopoly.app._
//import scala.concurrent.duration._
//import scala.concurrent.ExecutionContext.Implicits.global
//
////@TODO
///*
// * Alle tests müssen nach einer aktion nochmals mit einem get pruefen
// * ob die Werte wirklich gesetzt wurden, dies fehlt an vielen Stellen.
// * Testen des Status sagt nur teilweise ueber die rest Schnittstelle aus aber nicht viel ueber die Logik.
// */
//
//class AsyncAndSpeed extends FunSuite with BeforeAndAfter {
//
//  //JSON stuff
//  implicit val jsonFormats = DefaultFormats
//
//  //Debugging stuff
//  val BODY_MESSAGE = " BODY EMPTY?"
//  val JSON_MESSAGE = " JSON ERROR"
//  val EMPTY_MESSAGE = " SHOULD BE EMPTY"
//  val TIMEOUT = 2 seconds
//
//  //@TODO Check if Port already used in jetty server?
//  //@TODO error messages port alreadyin use
//  //Jetty server(restart) stuff
//  var server = JettyServer().startOnFreePort()
//  Global.default_url = "http://localhost:" + server.port
//  Global.testMode = true
//  var default_url = Global.default_url
//
//  after {
//    AsyncTestServlet.reset()
//  }
//
//  //@TODO clean description
//  // server speicher reihenfolge ab und ich frage sie zum schluss ab
//  // um zu schauen ob sie asynchon abgerarbeitet werden
//
//  test("1000 Sync Requests - Responses are ordered") {
//    val range = 0 until 1000
//    var res = List[Int]()
//    for (i <- range) {
//      val response = put(default_url + "/test/sync?number=" + i, TIMEOUT)
//      assert(response.status == 200)
//      res :+= i
//    }
//
//    get(default_url + "/test/numbers") map { response =>
//      assert(response.status == 200)
//      val numbers = parse(response.body).extract[Numbers].numbers
//      assert(numbers == range.toList)
//      assert(numbers == res)
//    }
//  }
//
//  test("1000 Async Requests - Responses have no order") {
//    val range = 0 to 1000
//    var res = List[Int]()
//    for (i <- range) {
//      put(default_url + "/test/async?number=" + i) map { response =>
//        assert(response.status == 200)
//        res :+= i
//      }
//    }
//
//    get(default_url + "/test/numbers") map { response =>
//      assert(response.status == 200)
//      val numbers = parse(response.body).extract[Numbers].numbers
//      //@INFO Koennte in manchen faellen fehlschlagen ~ Zufall
//      assert(numbers != range.toList)
//      assert(numbers == res)
//    }
//
//  }
//}