package de.vs.monopoly.service

import de.alexholly.util.http._
import de.alexholly.util.tcpsocket._
import de.vs.monopoly.logic.Player
import org.json4s._
import play.api.Logger
import org.json4s.jackson.JsonMethods._
import org.json4s.native.Serialization.{read, write}

/**
 * Created by alex on 12.12.15.
 */

case class PingService() extends IOnDisconnect with IOnConnect {

  protected implicit val jsonFormats: Formats = DefaultFormats
  val TIMEOUT = 2000
  ServerKomponenteFacade.addOnDisconnect(this)
  ServerKomponenteFacade.addOnConnect(this)

  case class ID(id: String)

  def onConnect(user: User) = {
    Logger.info("Player connected: " + user.id)
    //    Logger.info("player is dead " + user.name)
//    var response = HttpSync.delete("http://localhost:4567/player/" + user.name, TIMEOUT)
    var body = write(ID(user.id))
    user.senden("POST /id HTTP/1.1 \r\n" + "Content-Type: application/json; charset=UTF-8\r\n\r\n"+ body)
  }

  //Ping players
  def onDisconnect(user: User) = {
    //Logger.info("ping players")
    Logger.info("player is dead " + user.id)
    var response = HttpSync.delete("http://localhost:4567/player/" + user.id, TIMEOUT)
    println(response.status)
  }

}