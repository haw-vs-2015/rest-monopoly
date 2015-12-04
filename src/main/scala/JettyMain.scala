import de.alexholly.util.JettyServer

object JettyMain {

  def main(args: Array[String]): Unit = {
    JettyServer().startOnDefaultPort()
  }
}