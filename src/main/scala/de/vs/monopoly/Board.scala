package de.vs.monopoly

object Boards {

  var boards: Map[String, Board] = Map()

  // Gewürfelter Wert
  def rolled(_throw: Throw) {
    println(_throw)
  }

  def addBoard(gameid: String) = {
    boards += (gameid -> Board(List()))
  }

  //delete, wie loescht man das ganze game?
  def deleteBoard(gameid: String) = {
    boards -= gameid
  }

  def gameBoard(gameid: String): Option[Board] = boards.get(gameid)
  
  //Muell
  def getPlayers(gameid: String): List[Player] = boards.get(gameid) match {
    case Some(board) =>
      var lst: List[Player] = List()
      for (f <- board.fields) {
        lst = lst ::: f.players ::: Nil
      }
      lst
    case None => Nil
  }

  def apply() = boards.values

}

case class Board(fields: List[Field])

//get /boards/{gameid} was soll das mit dem place und wieso ist da noch ein name
case class Field(place: Place, players: List[Player])
case class Place(name: String)

case class Throw(roll1: Roll, roll2: Roll)

