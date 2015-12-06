package de.vs.monopoly.logic

/**
 * Created by alex on 23.11.15.
 */
object Events {
  //@TODO Second chance to receive data timeout...
  //@TODO Send data to all async if something in messages?

  var channels = Map[String, List[Subscriber]]()
  //channelname,subsribers
  var messages = Map[String, List[String]]() //channelname, message

  def addChannel(name: String): List[Subscriber] = {
    if (!channels.contains(name)) {
      val lst = List[Subscriber]()
      channels += (name -> lst)
      lst
    } else {
      channels(name)
    }
  }

  def removeChannel(name: String) {
    channels.get(name) match {
      case Some(subsribers) =>
        if (subsribers.size == 0) channels -= name
      case None =>
    }
  }

  def addSubscriber(channelName: String, subscriber: Subscriber) {
    addChannel(channelName) +:= subscriber
  }

  def removeSubscriber(channelName: String, subscriber: Subscriber): Unit = {
    channels.get(channelName) match {
      case Some(subsribers) =>
        channels += (channelName -> subsribers.filterNot { sub => sub == subscriber })
    }
    removeChannel(channelName)
  }

  def getSubscribers(channelName: String): Option[List[Subscriber]] = {
    channels.get(channelName) match {
      case Some(subsribers) => if (subsribers.size > 0) Some(subsribers) else None
      case None => None
    }
  }
}

case class Subscriber(name: String, uri: String)
case class Channels(events: List[Event])
case class Message(name: String, reason: String, sourceURI: String, player: Player)
