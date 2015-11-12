package de.vs.monopoly

import scala.collection.mutable.ListBuffer

object Decks {
  
  var chances: ListBuffer[Card] = ListBuffer()
  var communities: ListBuffer[Card] = ListBuffer()

  //@TODO ?
  //Init Cards need some random/shuffle
  //ohne id alles anhand name? haben alle karten einen anderen namen?
  //was löst die logik aus.
    //chances
    chances += Card("Go to jail", "blabla")

    
    //community
    communities += Card("Go to jail", "blabla")
  
  //methods
  def chance():Option[Card]  = {
    chances.headOption
  }
  
  def community():Option[Card]  = {
    communities.headOption
  }
}

case class Card(name:String, text:String) {
  
}