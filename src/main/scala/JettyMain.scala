import de.alexholly.util.JettyServer
import de.vs.monopoly.app.GameServlet
import de.vs.monopoly.logic.Games

object JettyMain {

  def main(args: Array[String]): Unit = {

    JettyServer().startOnDefaultPort()
    val t = new java.util.Timer()

//    val task = new java.util.TimerTask {
//      def run() = GameServlet.ping()
//    }
//    t.schedule(task, 3000L, 3000L)
  }

}