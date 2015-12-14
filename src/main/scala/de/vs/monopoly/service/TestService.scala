package de.vs.monopoly.service

import de.alexholly.util.tcpsocket.{IService, ServerKomponenteFacade, User}
import org.json4s._
import org.json4s.jackson.JsonMethods._

/**
 * Created by alex on 12.12.15.
 */
case class TestService() extends IService {

  protected implicit val jsonFormats: Formats = DefaultFormats

  def request(verb: String, url: String, params: String, body: String, user: User) {
    println("verb " + verb)
    println("url " + url)
    println("params " + params)
    println("body " + body)
    //user.senden("POST /player/turn HTTP/1.1\r\n\r\n")#

//    if(user.name=="") {
//      ServerKomponenteFacade.addID(parse(body).extract[Name].name, user)
//    }

    //Erfolgreich angemeldet
    user.senden("HTTP/1.1 200 Ok\r\n" + "Content-Type: application/json; charset=UTF-8\r\n\r\n")
  }

  def response(verb: String, url: String, params: String, body: String, user: User) {
    //Auf anfrage antworten
    //user.senden("HTTP/1.1 200 Ok\r\n" + "Content-Type: application/json; charset=UTF-8\r\n\r\n")
  }
}

case class Name(name: String)