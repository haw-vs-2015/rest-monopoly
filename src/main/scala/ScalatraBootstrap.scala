
import de.alexholly.util.http.HttpSync
import de.alexholly.util.IPManager
import de.alexholly.util.tcpsocket.ServerKomponenteFacade

import de.vs.monopoly.app._
import de.vs.monopoly.logic.Service
import de.vs.monopoly.service.{PingService, TestService}
import org.json4s._
import org.json4s.native.Serialization.write

import org.scalatra._
import javax.servlet.ServletContext
import play.api.Logger

import scala.concurrent.duration._

class ScalatraBootstrap extends LifeCycle {

  protected implicit val jsonFormats: Formats = DefaultFormats

  override def init(context: ServletContext) {
    context mount(new DiceServlet(), "/dice/*")
    context mount(new PlayerServlet(), "/player/*")
    context mount(new BoardServlet(), "/boards/*")
    context mount(new GameServlet(), "/games/*")
    context mount (new MessagesServlet(), "/messages/*")

    ServerKomponenteFacade.starten(3560)
    ServerKomponenteFacade.setMaxClients(2)

//    ServerKomponenteFacade.addService("name", TestService())
    PingService()

    //context mount(new PingServlet, "/connect/*")
//    val boardService = Service("abh928boards", "Manage Board", "boards", "http://" + IPManager.getLocalIP() + ":4567/boards")
//    val gameService = Service("abh928games", "Manage game", "games", "http://" + IPManager.getLocalIP() + ":4567/games")
//
//    var response = HttpSync.post("https://vs-docker.informatik.haw-hamburg.de/ports/8053/services",
//      write(boardService),
//      10 seconds,
//      ("content-type" -> "application/json"))
//
//    Logger.info("Erstelle BoardsService " + response.status)
//
//    response = HttpSync.post("https://vs-docker.informatik.haw-hamburg.de/ports/8053/services",
//      write(gameService),
//      10 seconds,
//      ("content-type" -> "application/json"))
//    Logger.info("Erstelle GamesService  " + response.status)

  }
}