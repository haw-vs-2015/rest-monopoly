package de.vs.monopoly.logic

object Players {
  
  //variables
  var _id = 0

  //methods
  def id(): String = { _id += 1; _id.toString }
  
}

//@TODO ?
//Wieso steht bei dem example /games/{gameid}/players keine position und place dabei? 
case class Player(id: String, name: String, gameid:String, uri: String, place: Place = Place(), var position: Int = 0, var ready: Boolean = false)

case class PlayerURI(id: String, name:String, gameid:String, uri: String)

case class Players(players:List[Player])