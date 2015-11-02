package de.vs.monopoly

case class Board(fields:List[Field])

//get /boards/{gameid} was soll das mit dem playce und wies ist da noch ein name
case class Field(place:Place, players:List[Player])
case class Place(name:String)
