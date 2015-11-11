package de.vs.monopoly

//object Places {
//  val placesConf = List[Place](
//      
//      Place("VINE STREET", "BROWN", 15),
//      Place("COMMUNITY CHEST", "BROWN", 15),
//      Place("CONVENTRY STREET", "BROWN", 15),
//      Place("COMMUNITY CHEST"),
//  
//  )
//}

object Boards {

  var boards: Map[String, Board] = Map()

  // Gewürfelter Wert
  def rolled(gameid: String, player: Player, _throw: Throw): Option[BoardStatus] = {
    var amount = _throw.roll1.number + _throw.roll2.number
    gameBoard(gameid) match {
      case Some(board) =>
        board.fields.foreach { field => field.players = field.players.filterNot(p => p.id == player.id) }
        player.position += amount //zurücketzen...
        board.fields(player.position).players +:= player
        Some(BoardStatus(player, board))
      case None => None
    }
    // Wer hat es gewürfelt
    // Wer hat den mutex, game fragen
    // bewegen des spielers

    // println(_throw)
    // None
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

case class Post(_throw: Throw, player:Player)

case class Board(fields: List[Field])
case class BoardStatus(player:Player, board: Board)

//get /boards/{gameid} was soll das mit dem place und wieso ist da noch ein name
case class Field(place: Place, var players: List[Player])

case class Place(name: String = "")

case class Throw(roll1: Roll, roll2: Roll)

