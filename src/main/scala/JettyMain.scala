import de.alexholly.util.JettyServer
import de.vs.monopoly.app.GameServlet
import de.vs.monopoly.logic.{Global, Games}
import play.api.Logger

object JettyMain {

  def main(args: Array[String]): Unit = {
    JettyServer().startOnDefaultPort()
//    Logger.info("ARGS: " + args.size)

//    if (args.size == 0) {
//      //local
//    } else if (args.size == 1 && args(0) == "public") {
//      Global.init(true)
//    }

    //    val t = new java.util.Timer()
  }
}