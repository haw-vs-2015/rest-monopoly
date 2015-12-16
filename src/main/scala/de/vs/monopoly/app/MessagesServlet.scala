package de.vs.monopoly.app

import de.alexholly.util.http.HttpAsync
import de.alexholly.util.tcpsocket.ServerKomponenteFacade
import de.vs.monopoly.logic.{Message, Subscriber, Messages}
import org.json4s._
import org.scalatra._
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.scalate.ScalateSupport
import play.api.Logger

class MessagesServlet extends ScalatraServlet with ScalateSupport with JacksonJsonSupport {

  protected implicit val jsonFormats: Formats = DefaultFormats

  protected override def transformRequestBody(body: JValue): JValue = body.camelizeKeys

  before() {
    contentType = formats("json")
    response.headers += ("Access-Control-Allow-Origin" -> "*")
  }

  //@TODO HTTP url, wie ist der Standard? /name1/id1/name2/id2 ?
  //oder ist dies egal?
  //gets all Channels and subsribers for the channels
  get("/") {
    Messages.getChannels()
  }

  //@TODO needs testing
  //gets a Channel and subsribers for that channel
  get("/:channel") {
    Messages.getChannel()
  }

  //@TODO needs testing
  //gets a Subscriber and with all his subscribt channels
  get("/subscriber/:subscriber") {
    Messages.getSubscriber()
  }

  //A user subscribes a channel he is interested in.
  post("/subscribe/:channel") {
    val subscriber = parse(request.body).extract[Subscriber]
    subscriber.uri += "/messages/send/" + params("channel")
    Messages.addSubscriber(params("channel"), subscriber)
    Ok()
  }

  //Send a message to all subscribers of a channel
  post("/send/:channel") {
    Messages.getSubscribers(params("channel")) match {
      case Some(subscribers) =>
        parse(request.body).extractOpt[Message] match {
          case Some(message) =>
            for (subscriber <- subscribers) {
              Logger.info("sending to - " + subscriber.id)
              ServerKomponenteFacade.senden(subscriber.id, "POST /messages/send/" + params("channel") + " HTTP/1.1\r\n" + "Content-Length: " + request.body.length + "\r\nContent-Type: application/json; charset=UTF-8\r\n\r\n" + request.body)
//              HttpAsync.post(subscriber.uri, request.body)
            }
          case None => NotFound("Invalid body/json")
        }
      case None => Accepted("Keiner hört dir zu...")
    }
  }

  //@TODO needs testing
  //Remove a subscriber from a channel.
  delete("/:channel/:subscriber") {
    Messages.removeSubscriber(params("channel"), params("subscriber"))
  }

  //@TODO needs testing
  //Remove a subscriber from all channels.
  delete("/channel/all/:subscriber") {
    Messages.removeSubscriber(params("subscriber"))
  }
}
