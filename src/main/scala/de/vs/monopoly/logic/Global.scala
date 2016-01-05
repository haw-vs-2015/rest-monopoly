package de.vs.monopoly.logic

import de.alexholly.util.IPManager

/**
 * Created by alex on 28.11.15.
 */

object Global {

  var testMode = false

  var test_url = "http://localhost:4567"
  var games_uri = "http://localhost:4567"
  var boards_uri = "http://localhost:4567"
  var messages_uri = "http://localhost:4567"

  //@TODO setup jetty correctly
//  def init(public: Boolean) {
//    if (public) {
//      games_uri = "http://" + IPManager.getInternetIP() + ":4567"
//      boards_uri = "http://" + IPManager.getInternetIP() + ":4567"
//      messages_uri = "http://" + IPManager.getInternetIP() + ":4567"
//    }
//  }
}
