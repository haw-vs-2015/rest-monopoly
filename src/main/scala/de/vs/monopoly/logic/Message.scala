package de.vs.monopoly.logic

/**
 * Created by alex on 23.11.15.
 */

import play.api.Logger

object Messages {

  def getSubscriber(): Any = ???

  def getChannel(): Any = ???

  //@TODO Messages are send once right now. No checking if they are succefully send.
  //@TODO Second chance to receive data timeout...
  //@TODO Send data to all async if something in messages?
  //@TODO Send to all subscribers in all channels
  //@TODO remove all subscibtion of a subscriber
  //@TODO List of all channels
  //@TODO List of all subscribt channels of a subscriber

  var subscribers = Map[String, Subscriber]()
  //id, object
  var channels = Map[String, List[String]]()
  //channelname, ids ob subscribers
  //  var channels = Map[String, List[Subscriber]]()
  var messages = Map[String, List[String]]() //channelname, message

  //  def addChannel(name: String) {
  //    if (!channels.contains(name)) {
  //      val lst = List[Subscriber]()
  //      channels += (name -> lst)
  //      Logger.info("addChannel - " + name)
  //    }
  //  }

  def addChannel(channelName: String) {
    if (!channels.contains(channelName)) {
      val lst = List[String]()
      channels += (channelName -> lst)
      Logger.info("addChannel - " + channelName)
    }
  }

  def removeChannel(channelName: String) {
    channels.get(channelName) match {
      case Some(subsribers) =>
        if (subsribers.size == 0) {
          channels -= channelName
          Logger.info("removeChannel - " + channelName + " wurde entfernt")
        } else {
          Logger.info("removeChannel - " + channelName + " hat noch subscriber")
        }
      case None =>
        Logger.info("removeChannel - " + channelName + " not found.")
    }
  }

  //@TODO Needs Thread safe
  def addSubscriberToChannel(channelName: String, subscriber_id: String) {
    val subscribers = channels.get(channelName).get
    if (!subscribers.contains(subscriber_id)) {
      val new_subscribers = subscribers:+(subscriber_id)
      channels += (channelName -> new_subscribers)
    }
  }

  //@TODO Doppelter Name in einem Channel, darf nicht möglich sein
  //@TODO Channel Typ einbauen, damit kein doppelten subsciben eines bestimmten typs möglch ist
  //Dadurch muss das unsubscribe nicht ausgeführt werden.
  def addSubscriber(channelName: String, subscriber: Subscriber) {
    addChannel(channelName)

    //Add first channel to subscriber list
    subscriber.subscribt_channels +:= channelName

    //@DONE needs testing - entfernen der doppelten channels aus der subscriber channel list
    subscriber.subscribt_channels = subscriber.subscribt_channels.distinct

    subscribers += (subscriber.id -> subscriber)

    //@DONE join all channels from subscriber
    for (wantChannel <- subscriber.subscribt_channels) {
      addSubscriberToChannel(wantChannel, subscriber.id)
    }
    Logger.info("addSubscriber - " + subscriber)
  }

  def removeSubscriber(channelName: String, subscriber: String) {
    channels.get(channelName) match {
      case Some(subsribers) =>
        channels += (channelName -> subsribers.filterNot { sub => sub == subscriber })
        removeChannel(channelName)
        Logger.info("removeSubscriber - " + subscriber + " wurde entfernt")
      case None =>
        Logger.info("removeSubscriber - " + subscriber + " den channel gibt es nicht")
    }
  }

  def removeSubscriber(subscriberName: String) {
    getSubscriber(subscriberName) match {
      case Some(subscriber) =>
        for (isInChannel <- subscriber.subscribt_channels) {
          removeSubscriber(isInChannel, subscriberName)
        }
        Logger.info("removeSubscriber - " + subscriberName + " wurde entfernt.")
      case None =>
        Logger.info("removeSubscriber - " + subscriberName + " gibt es nicht.")
    }
  }

  def getSubscribersID(channelName: String): Option[List[String]] = {
    Logger.info("getSubscribersID - " + channels.get(channelName))
    channels.get(channelName)
  }

  def getSubscribers(channelName: String): Option[List[Subscriber]] = {
    channels.get(channelName) match {
      case Some(subscriberids) =>
        Logger.info("getSubscribers - channel " + channelName + " gefunden.")
        Some(subscribers.values.toList)
      case None =>
        Logger.info("getSubscribers - channel " + channelName + " gibt es nicht.")
        None
    }
  }

  def getSubscriber(subscriberName: String): Option[Subscriber] = {
    Logger.info("getSubscriber - " + subscriberName)
    subscribers.get(subscriberName)
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

case class Subscriber(id: String, var subscribt_channels: List[String], var uri: String)

case class Channels(channels: List[Channel])

case class Channel(name: String, subscribers: List[String])

case class Message(id: String, reason: String, sourceURI: String, payload: String)