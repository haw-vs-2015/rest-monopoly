import de.alexholly.util.JettyServer
import de.vs.monopoly.app.GameServlet
import de.vs.monopoly.logic.Games

object JettyMain {

  def main(args: Array[String]): Unit = {
    JettyServer().startOnDefaultPort()
//    val t = new java.util.Timer()
  }

}