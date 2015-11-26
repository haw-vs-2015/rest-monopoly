package de.vs.monopoly

object Boards {

  var boards: Map[String, Board] = Map()

  // Gewürfelter Wert
  def rolled(gameid: String, playerid: String, currPlayerid: String, _throw: Throw): Option[BoardStatus] = {
    //@TODO Holen ueber client Anfrage von games-service
    //    Games.getCurrentPlayer(gameid) match {
    //      case Some(player) =>
    if (currPlayerid == playerid) {
      //falls der korrekte player
      var amount = _throw.roll1.number + _throw.roll2.number
      boards.get(gameid) match {
        case Some(board) =>
          getPlayer(gameid, playerid) match {
            case Some(playerLocation) =>
              board.fields.foreach { field =>
                field.players = field.players.filterNot(p => p.id == playerLocation.id)
              }
              //@TODO Zuruecksetzen modulo...
              playerLocation.position += amount
              board.fields(playerLocation.position).players :+= playerLocation //neue position auf feld setzen
              Some(BoardStatus(playerLocation, board, Event()))
            case None => Some(BoardStatus(getPlayer(gameid, playerid).get, board, Event()))
          }

        case None => None
      }
    } else {
      None
    }
    // Wer hat es gewürfelt
    // Wer hat den mutex, game fragen
    // bewegen des spielers

    // println(_throw)
    // None
  }

  def putPlayerToBoard(gameid: String, playerid: String): Option[String] = {
    boards.get(gameid) match {
      case Some(board) =>
        if (board.fields.isDefinedAt(0)) {
          board.fields.head.players :+= PlayerLocation(playerid, "", 0)
          println("spieler " + playerid + " wurde dem board " + gameid + " hinzugefuegt.")
          Some(playerid)
        } else {
          println("board wurde nicht korrekt initialisiert.")
          None
        }
      case None =>
        println("board " + gameid + " nicht gefunden für spieler " + playerid)
        None
    }
  }

  def get(gameid: String): Option[Board] = boards.get(gameid)

  def addBoard(gameid: String): Option[String] = {
    if (!boards.contains(gameid)) {
      boards += (gameid -> Board(Fields().fieldConf))
      println("board wurde für game " + gameid + " erstellt.")
      Some("")
    } else {
      println("game " + gameid + " bereits in boards vorhanden.")
      None
    }
  }

  //@TODO delete, wie loescht man das ganze game?
  def deleteBoard(gameid: String): Option[String] = {
    if (boards.contains(gameid)) {
      boards -= gameid
      println("board/game " + gameid + " wurde geloescht.")
      Some("")
    } else {
      println("board/game" + gameid + " NotFound!")
      None
    }
  }

  def deletePlayer(gameid: String, playerid: String) = boards.get(gameid) match {
    case Some(board) =>
      //@TODO iterate until delete performance
      for (f <- board.fields) {
        f.players = f.players.filterNot(x => playerid == x.id)
      }
      println("spieler " + playerid + " wurde von board/game" + gameid + " geloescht")
      Some(playerid)
    case None =>
      println("spieler " + playerid + " konnte nicht geloescht werden board/game" + gameid + " NotFound!")
      None
  }

  def getPlayer(gameid: String, playerid: String) = boards.get(gameid) match {
    case Some(board) =>
      var playerLocation: PlayerLocation = null
      //@TODO kompakte schreibweise die alle faelle abdeckt suchen
      for (f <- board.fields) {
        f.players.find(x => playerid == x.id) match {
          case Some(player) => println("spieler " + playerLocation + " wurde auf board/game " + gameid + " gefunden!")
            playerLocation = player
          case None => None
        }
      }

      Some(playerLocation)
    case None =>
      println("spieler " + playerid + " konnte nicht gefunden werden board/game " + gameid + " NotFound!")
      None
  }

  //@TODO PlayerLocationMap sollte vorhanden sein und in Board fields sollten nur referenzen auf die PlayerLocationMap liegen.
  def getPlayers(gameid: String): List[PlayerLocation] = boards.get(gameid) match {
    case Some(board) =>
      var lst: List[PlayerLocation] = List()
      for (f <- board.fields) {
        lst = lst ::: f.players ::: Nil
      }
      lst
    case None => Nil
  }

  def reset(): Unit = {
    boards = Map()
  }

  def apply() = new Boards(boards.values.toList)

}

case class Post(_throw: Throw, player: PlayerLocation)

case class Boards(boards: List[Board])

case class Board(fields: List[Field])

case class BoardStatus(player: PlayerLocation, board: Board, events: Event)

case class Event()

//@TODO ?
//get /boards/{gameid} was soll das mit dem place und wieso ist da noch ein name
case class Field(place: Place, var players: List[PlayerLocation])

case class Place(name: String = "")

case class PlayerLocation(id: String, place: String, var position: Int)

case class Throw(roll1: Roll, roll2: Roll)

//@TODO Places definieren
//40 felder

case class Fields() {
  var fieldConf = List[Field]()
  for (place <- Places.placesConf) {
    fieldConf :+= Field(place, List[PlayerLocation]())
  }
}

object Places {
  val placesConf = List[Place](
      Place("0 - los"),
      Place("1"),
      Place("2"),
      Place("3"),
      Place("4"),
      Place("4"),
      Place("5"),
      Place("6"),
      Place("7"),
      Place("8"),
      Place("9"),
      Place("10"),
      Place("11"),
      Place("12"),
      Place("13"),
      Place("14"),
      Place("15"),
      Place("16"),
      Place("17"),
      Place("18"),
      Place("19"),
      Place("20"),
      Place("21"),
      Place("22"),
      Place("23"),
      Place("24"),
      Place("25"),
      Place("26"),
      Place("27"),
      Place("28"),
      Place("29"),
      Place("30"),
      Place("31"),
      Place("32"),
      Place("33"),
      Place("34"),
      Place("35"),
      Place("36"),
      Place("37"),
      Place("38"),
      Place("39"),
      Place("40")
  )
}