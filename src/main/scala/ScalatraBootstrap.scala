
import de.alexholly.util.http.HttpSync
import de.alexholly.util.IPManager
import de.vs.monopoly.Service
import org.json4s._
import org.json4s.native.Serialization.write

import de.vs.rest.server.monopoly.app._
import org.scalatra._
import javax.servlet.ServletContext
import scala.concurrent.duration._

class ScalatraBootstrap extends LifeCycle {

  protected implicit val jsonFormats: Formats = DefaultFormats

  override def init(context: ServletContext) {
    //@TODO Check Async bei multible servlets see http://www.smartjava.org/content/tutorial-getting-started-scala-and-scalatra-part-iv
    //ganz unten

    context mount(new DiceServlet(), "/dice/*")
    context mount(new PlayerServlet(), "/player/*")
    context mount(new BoardServlet(), "/boards/*")
    context mount(new GameServlet(), "/games/*")

    val boardService = Service("abh928boards", "Manage Board", "boards", "http://" + IPManager.getLocalIP() + ":4567/boards")

    val gameService = Service("abh928games", "Manage game", "games", "http://" + IPManager.getLocalIP() + ":4567/games")

    var response = HttpSync.post("https://vs-docker.informatik.haw-hamburg.de/ports/8053/services",
      write(boardService),
      10 seconds,
      ("content-type" -> "application/json"))

    println("Erstelle BoardsService " + response.status)

    response = HttpSync.post("https://vs-docker.informatik.haw-hamburg.de/ports/8053/services",
      write(gameService),
      10 seconds,
      ("content-type" -> "application/json"))
    println("Erstelle GamesService  " + response.status)

    //context mount (new EventServlet(), "/events/*")
    context.mount(new AsyncTestServlet(), "/test/*")
  }
}