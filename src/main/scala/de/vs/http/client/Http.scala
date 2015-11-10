package de.vs.http.client

/**
 * Created by alex on 10.11.15.
 */

import dispatch._, Defaults._

object MonopolyRESTClient {

  val svc = url("http://www.google.de")
  val country = Http(svc OK as.String)
  country.map { response =>
    println(response)
  }

  def getPlayers() {

  }
}