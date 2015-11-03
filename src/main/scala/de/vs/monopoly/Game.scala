package de.vs.monopoly

//import scala.collection.mutable.ListBuffer

object Games { //GamesFacade

  //variables
  var _id = 0
  var games: Map[String, Game] = Map()

  //methods
  def id(): Int = { _id += 1; _id }

  def createNewGame(): Game = {
    
    //Init Components //Generate Components fehlt
    val _game = ""
    val dice = ""
    val board = ""
    val bank = ""
    val broker = ""
    val decks = ""
    val events = ""
    val _components = Components(_game, dice, board, bank, broker, decks, events)
    
    
    var game = Game( components = _components )
    games += (game.gameid -> game)
    game
  }

  def getGame(gameid: String):Option[Game] = {
    games.get(gameid)
  }

  def apply() = new Games(games) //getGames
}


case class Components(game:String, dice:String, board:String, bank:String, broker:String, decks:String, events:String)
case class Games(games: Map[String, Game])

//get /boards wieso enthält ein Game kein ready und players?
case class Game(gameid: String = Games.id.toString, players: List[Player] = List(), components:Components, started: Boolean = false) {
  override def toString() = "{ \"gameid\":" + "\"" + gameid + "\"" + "}" //Muell
}