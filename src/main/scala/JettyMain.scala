import org.eclipse.jetty.server._
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener

object JettyMain {

  def main(args: Array[String]): Unit = {
    JettyServer().start()
  }
}
