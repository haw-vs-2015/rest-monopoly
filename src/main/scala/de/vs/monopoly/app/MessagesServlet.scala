package de.vs.monopoly.app

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

  //ID'S Low Lvl

  //@TODO HTTP url, wie ist der Standard? /name1/id1/name2/id2 ?
  //oder ist dies egal?
  //gets all Channels and subsribers for the channels
  get("/") {
    Messages.getChannels()
  }

  //@TODO needs testing
  //gets a Channel and subsribers for that channel
  get("/:channel") {
    Messages.getChannelSubscribers(params("channel"))
  }

  //@TODO needs testing
  //gets all Subscribers with his subscribt channels
  get("/subscriber/:subscriber") {
    Messages.getSubscriber(params("subscriber"))
  }

  //  //@TODO needs testing
  //  //gets a Subscriber with all his subscribt channels
  //  get("/subscriber/:subscriber") {
  //    Messages.getSubscribers(params("subscriber"))
  //  }

  //A user subscribes a channel he is interested in.
  post("/subscribe/:channel") {
    val subscriber = parse(request.body).extract[Subscriber]
    subscriber.uri += "/messages/send/" + params("channel")
    Messages.addSubscriber(params("channel"), subscriber)
  }

  //Send a message to all subscribers of a channel
  post("/send/:channel") {
    val subscribers = Messages.getChannelSubscribers(params("channel")).subscribers

    if (subscribers.size > 0) {

      val body = request.body
      parse(body).extractOpt[Message] match {

        case Some(message) =>
          val post = "POST /messages/send/" + params("channel") + " HTTP/1.1\r\n" + "Content-Length: " + body.length + "\r\nContent-Type: application/json; charset=UTF-8\r\n\r\n" + body
          println(subscribers)
          for (subscriber <- subscribers) {
            Logger.info("sending to - " + subscriber)
            ServerKomponenteFacade.senden(subscriber, post)
          }

        case None => NotFound("Invalid body/json")
      }
    } else {
      Accepted("Keiner hört dir zu...")
    }
  }

  //@TODO needs testing
  //Remove a subscriber from a channel.
  delete("/:channelid/subscriber/:subscriberid") {
    Messages.removeSubscriberFromChannel(params("channelid"), params("subscriberid"))
  }

  //@TODO needs testing
  //Remove a subscriber from all channels.
  delete("/subscriber/:subscriberid") {
    Messages.removeSubscriberFromAllChannels(params("subscriberid"))
  }
}
