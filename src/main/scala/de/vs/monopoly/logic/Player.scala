package de.vs.monopoly.logic

object Players {
  
  //variables
  var _id = 0

  //methods
  def id(): String = { _id += 1; _id.toString }
  
}

//@TODO ?
//Wieso steht bei dem example /games/{gameid}/players keine position und place dabei? 
case class Player(id: String, name: String, uri: String, place: Place = Place(), var position: Int = 0, var ready: Boolean = false)