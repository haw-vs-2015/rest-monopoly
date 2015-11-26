import java.net.BindException
import java.util.logging.Level

import de.vs.rest.server.monopoly.app.{GameServlet, BoardServlet}
import org.eclipse.jetty.server._
import org.eclipse.jetty.util.log.Logger
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
  var portOffset = 0
  var port = 4567
  var serverStarted = true


  def startOnFreePort(): JettyServer ={
    server = new Server()
    var _connector = new ServerConnector(server)
    server.setConnectors(Array[Connector] { _connector })

    val webapp = conf.webapp
    val webApp = new WebAppContext
    webApp setContextPath conf.contextPath
    webApp setResourceBase conf.webapp
    webApp setEventListeners Array(new ScalatraListener)
    server setHandler webApp

    server.start()
    port = _connector.getLocalPort()

    this
  }

  def startOnDefaultPort(): JettyServer = {
    server = new Server
    server setStopTimeout 0
    //server setDumpAfterStart true
    server setStopAtShutdown true

    val httpConfig = new HttpConfiguration()
    httpConfig setSendDateHeader true
    httpConfig setSendServerVersion false

    val connector = new NetworkTrafficServerConnector(server, new HttpConnectionFactory(httpConfig))
    connector setPort (conf.port + portOffset)
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
    serverStarted = true
    port = (conf.port + portOffset)

    println("started jetty on port " + port)
    this
  }
}
