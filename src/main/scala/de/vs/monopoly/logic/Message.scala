package de.vs.monopoly.logic

/**
 * Created by alex on 23.11.15.
 */

import play.api.Logger

object Messages {
  //@TODO Messages are send once right now. No checking if they are succefully send.
  //@TODO Second chance to receive data timeout...
  //@TODO Send data to all async if something in messages?
  //@TODO Send to all subscribers in all channels
  //@TODO Doppelter Name in einem Channel

  var channels = Map[String, List[Subscriber]]()
  var messages = Map[String, List[String]]() //channelname, message

  def addChannel(name: String) {
    if (!channels.contains(name)) {
      val lst = List[Subscriber]()
      channels += (name -> lst)
      Logger.info("addChannel - " + name)
    }
  }

  def removeChannel(name: String) {
    channels.get(name) match {
      case Some(subsribers) =>
        if (subsribers.size == 0) channels -= name
        Logger.info("removeChannel - " + name)
      case None =>
    }
  }

  def addSubscriber(channelName: String, subscriber: Subscriber) {
    addChannel(channelName)
    channels += (channelName -> channels.get(channelName).get.:+(subscriber))
    Logger.info("addSubscriber - " + subscriber)
    Logger.info("addSubscriber - " + channels.get(channelName))
  }

  def removeSubscriber(channelName: String, subscriber: String) {
    channels.get(channelName) match {
      case Some(subsribers) =>
        channels += (channelName -> subsribers.filterNot { sub => sub.id == subscriber })
        Logger.info("removeSubscriber - " + subscriber)
      case None =>
    }

    removeChannel(channelName)
  }

  def getSubscribers(channelName: String): Option[List[Subscriber]] = {
    Logger.info("getSubscribers - " + channels.get(channelName))
    channels.get(channelName)
  }

  def getChannels(): Channels = {
    var rs = List[Channel]()
    for (channel <- channels) {
      rs +:= Channel(channel._1, channel._2)
    }
    Logger.info("getChannels - " + Channels(rs))
    Channels(rs)
  }

  def reset(): Unit = {
    channels = Map()
  }
}

case class Subscriber(id: String, var uri: String)

case class Channels(channels: List[Channel])

case class Channel(name: String, subscribers: List[Subscriber])

case class Message(id: String, reason: String, sourceURI: String, payload: String)