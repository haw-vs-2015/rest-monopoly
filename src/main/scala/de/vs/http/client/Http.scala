package de.vs.http.client

import play.api.Play.current
import play.api.libs.ws._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.ning.NingWSClient
import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{ Success, Failure }
import scala.xml._
import scala.concurrent._
import ExecutionContext.Implicits.global
import play.api.libs.oauth.OAuthCalculator
import play.api.libs.json._

/**
 * @version 0.0.2, 20.05.2015
 */

/**
 * Created by alex on 10.11.15.
 */
class Http {
    val TIMEOUT = 40000
//    val builder = new play.api.libs.ws.ning.NingWSClientConfig.Builder()
//    val client = new NingWSClient()
//
    case class Response(exception: Throwable, body: String, status: Int, statusText: String)

//    //TODO
//    //UTF 8 Formatierung einbauen
//    def request(url: String): Future[WSResponse] = {
//      client.url(url.replaceAll(" ", "%20")).withRequestTimeout(TIMEOUT).get()
//    }
//
//    def request(url: String, oauth: OAuthCalculator): Future[WSResponse] = {
//      if((url == null) || (oauth == null)) return null
//      client.url(url).sign(oauth).withRequestTimeout(TIMEOUT).get
//    }
//
//    def post(url: String, oauth: OAuthCalculator, data: JsObject): Future[WSResponse] = {
//      client.url(url).sign(oauth).withRequestTimeout(TIMEOUT).post(data)
//    }
//
//    def post(url: String, oauth: OAuthCalculator): Future[WSResponse] = {
//      client.url(url).sign(oauth).withRequestTimeout(TIMEOUT).post(Json.obj())
//    }
    //    Await.result(futureResponse, 4 seconds)
    //    sys.exit()
}
