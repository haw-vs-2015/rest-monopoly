import org.eclipse.jetty.server.{HttpConnectionFactory, NetworkTrafficServerConnector, HttpConfiguration, Server}
import org.eclipse.jetty.util.thread.QueuedThreadPool
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener

/**
 * Created by alex on 11.11.15.
 */

case class JettyServer() {

  object conf {
    val port = sys.env.get("PORT") map (_.toInt) getOrElse (4567)
    val stopTimeout = sys.env.get("STOP_TIMEOUT") map (_.toInt) getOrElse (0)
    val connectorIdleTimeout = sys.env.get("CONNECTOR_IDLE_TIMEOUT") map (_.toInt) getOrElse (0)
    val webapp = sys.env.get("PUBLIC") getOrElse "webapp"
    val contextPath = sys.env.get("CONTEXT_PATH") getOrElse "/"
  }

  var server: Server = null

  def stop(): JettyServer = {
    println("stop jetty")
    server.stop()
    this
  }

  def start(): JettyServer = {
    server = new Server

    server setStopTimeout 0
    //server setDumpAfterStart true
    server setStopAtShutdown true

    val httpConfig = new HttpConfiguration()
    httpConfig setSendDateHeader true
    httpConfig setSendServerVersion false

    val connector = new NetworkTrafficServerConnector(server, new HttpConnectionFactory(httpConfig))
    connector setPort conf.port
    connector setSoLingerTime 0
    connector setIdleTimeout conf.connectorIdleTimeout
    server addConnector connector

    val webapp = conf.webapp
    val webApp = new WebAppContext
    webApp setContextPath conf.contextPath
    webApp setResourceBase conf.webapp
    webApp setEventListeners Array(new ScalatraListener)

    server setHandler webApp

    server.start()

    println("started jetty")
    this
  }
}
