package de.vs.monopoly


//Wieso steht bei dem example /games/{gameid}/players keine position und place dabei? 
case class Player(id:String, name:String, uri:String, place:Place, position:Int, ready:Boolean) {
  
}