package de.alexholly.util

import org.eclipse.jetty.server._
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener
import play.api.Logger

/**
 * Created by alex on 11.11.15.
 */

case class JettyServer() {

  var server: Server = null
  var port = 4567
  var serverStarted = true

  object conf {
    val port = sys.env.get("PORT") map (_.toInt) getOrElse (4567)
    //run on public ip
    val host = sys.env.get("HOST") map (_.toString) getOrElse ("0.0.0.0")
    val stopTimeout = sys.env.get("STOP_TIMEOUT") map (_.toInt) getOrElse (0)
    val connectorIdleTimeout = sys.env.get("CONNECTOR_IDLE_TIMEOUT") map (_.toInt) getOrElse (0)
    val webapp = sys.env.get("PUBLIC") getOrElse "webapp"
    val contextPath = sys.env.get("CONTEXT_PATH") getOrElse "/"
  }

  def startOnFreePort(): JettyServer ={
    server = new Server()
    val _connector = new ServerConnector(server)
    server.setConnectors(Array[Connector] { _connector })

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
    server setStopAtShutdown true

    val httpConfig = new HttpConfiguration()
    httpConfig setSendDateHeader true
    httpConfig setSendServerVersion false

    val connector = new NetworkTrafficServerConnector(server, new HttpConnectionFactory(httpConfig))
    connector setPort (conf.port)
    connector setHost (conf.host)
    connector setSoLingerTime 0
    connector setIdleTimeout conf.connectorIdleTimeout
    server addConnector connector

    val webApp = new WebAppContext
    webApp setContextPath conf.contextPath
    webApp setResourceBase conf.webapp
    webApp setEventListeners Array(new ScalatraListener)
    server setHandler webApp

    server.start()
    serverStarted = true
    port = (conf.port)

    Logger.info("started jetty on port " + port)
    this
  }
}
