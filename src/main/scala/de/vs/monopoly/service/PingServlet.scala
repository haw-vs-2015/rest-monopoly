package de.vs.monopoly.service

import de.alexholly.util.http._
import de.alexholly.util.tcpsocket._
import de.vs.monopoly.logic.{Message, Games}
import org.json4s._
import org.scalatra.{NotFound, Ok}
import play.api.Logger
import org.json4s.jackson.JsonMethods._
import org.json4s.native.Serialization.{read, write}

/**
 * Created by alex on 12.12.15.
 */

case class PingService() extends IOnDisconnect with IOnConnect {

  protected implicit val jsonFormats: Formats = DefaultFormats
  val TIMEOUT = 5000
  ServerKomponenteFacade.addOnDisconnect(this)
  ServerKomponenteFacade.addOnConnect(this)

  case class ID(id: String)

  def onConnect(user: User) = {
    Logger.info("Player connected: " + user.id)
    //    Logger.info("player is dead " + user.name)
    //    var response = HttpSync.delete("http://localhost:4567/player/" + user.name, TIMEOUT)

//    var body = write(ID(user.id))
//    user.senden("POST /id HTTP/1.1 \r\n" + "Content-Type: application/json; charset=UTF-8\r\n\r\n" + body)

    var message = Message(user.id, "set_playerid", "EGAL", "")
    user.senden("POST /id HTTP/1.1 \r\n" + "Content-Type: application/json; charset=UTF-8\r\n\r\n" + write(message))

    //Dem spieler bei joinen die aktuelle Spielerliste senden
    val games = HttpSync.get("http://localhost:4567/games", TIMEOUT).body
    message = Message("SERVER", "update_games", "EGAL", games)
    user.senden("POST /id HTTP/1.1 \r\n" + "Content-Type: application/json; charset=UTF-8\r\n\r\n" + write(message))
//    HttpAsync.post("http://localhost:4567/messages/send/id", write(message))

  }

  //Ping players
  def onDisconnect(user: User) = {
    //Logger.info("ping players")
    Logger.info("player is dead " + user.id)
    Games findPlayer (user.id) match {
      case Some(player) =>
        HttpSync.delete("http://localhost:4567" + "/games/" + player.gameid + "/players/" + player.id, TIMEOUT)
        Ok()
      case None => NotFound()
    }
  }

}