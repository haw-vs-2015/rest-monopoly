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
  //@TODO remove all subscibtion of a subscriber
  //@TODO List of all channels
  //@TODO List of all subscribt channels of a subscriber
  //@TODO? removeAChannel and all his subscribers?

  var subscribers = Map[String, Subscriber]()
  //channelname, ids ob subscribers
  var channels = Map[String, List[String]]().withDefaultValue(Nil)
  //  var messages = Map[String, List[String]]() //channelname, message

  def getChannels(): Channels = {
    Channels(channels.keys.toList)
  }

  def getChannelSubscribers(channelName: String): Subscribers = {
    Subscribers(channels(channelName))
  }

  def getSubscriber(subscriberName: String): Option[Subscriber] = {
    Logger.info("getSubscriber - " + subscriberName)
    subscribers.get(subscriberName)
  }

  private def addChannel(channelName: String): List[String] = {
    if (!channels.contains(channelName)) {
      val lst = List[String]()
      channels += (channelName -> lst)
      Logger.info("addChannel - " + channelName)
      lst
    } else {
      channels(channelName)
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
  private def addSubscriberToChannel(channelName: String, subscriber_id: String) {
    var _subscribers = addChannel(channelName)
    if (!_subscribers.contains(subscriber_id)) {
      Logger.info("--laddSubscriber - " + _subscribers)
      _subscribers +:= subscriber_id
      channels += (channelName -> _subscribers)
    }
  }

  //@TODO Doppelter Name in einem Channel, darf nicht möglich sein
  //@TODO Channel Typ einbauen, damit kein doppelten subsciben eines bestimmten typs möglich ist
  //Dadurch muss das unsubscribe nicht ausgeführt werden.
  def addSubscriber(channelName: String, subscriber: Subscriber) {

    //Add first channel to subscriber list
    subscriber.subscribt_channels +:= channelName

    //@DONE needs testing - entfernen der doppelten channels aus der subscriber channel list
    subscriber.subscribt_channels = subscriber.subscribt_channels.distinct
    println(subscriber.subscribt_channels)
    subscribers += (subscriber.id -> subscriber)

    //@DONE join all channels from subscriber
    for (wantChannel <- subscriber.subscribt_channels) {
      addSubscriberToChannel(wantChannel, subscriber.id)
    }
    Logger.info("addSubscriber - " + subscribers)
    Logger.info("addSubscriber - " + getChannelSubscribers(channelName))
  }

  def removeSubscriberFromChannel(channelName: String, subscriber: String) {
    channels.get(channelName) match {
      case Some(subsribers) =>
        channels += (channelName -> subsribers.filterNot { sub => sub == subscriber })
        removeChannel(channelName)
        Logger.info("removeSubscriber - " + subscriber + " wurde entfernt")
      case None =>
        Logger.info("removeSubscriber - " + subscriber + " den channel gibt es nicht")
    }
  }

  def removeSubscriberFromAllChannels(subscriberName: String) {
    getSubscriber(subscriberName) match {
      case Some(subscriber) =>
        for (isInChannel <- subscriber.subscribt_channels) {
          removeSubscriberFromChannel(isInChannel, subscriberName)
        }
      case None =>
        Logger.info("removeSubscriber - " + subscriberName + " gibt es nicht.")
    }
  }

  def reset(): Unit = {
    channels = Map()
    subscribers = Map()
  }
}

case class Subscribers(subscribers: List[String])

case class Subscriber(id: String, var subscribt_channels: List[String], var uri: String)

case class Channels(channels: List[String])

case class Channel(channelName: String, subscribers: List[String])

case class Message(id: String, reason: String, sourceURI: String, payload: String)