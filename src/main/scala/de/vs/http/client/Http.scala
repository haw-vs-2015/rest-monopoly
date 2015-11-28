package de.vs.http.client

/**
 * Created by alex on 10.11.15.
 */

import com.ning.http.client.{HttpResponseStatus, AsyncHttpClientConfigBean}
import com.ning.http.client.providers.netty.response.NettyResponse
import org.scalatra.RequestTimeout
import play.api.libs.json.JsValue
import play.api.libs.ws.{WSCookie, WSResponse}
import play.api.libs.ws.ning.NingWSClient
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.xml.Elem

case class Timeout() extends WSResponse {
  var status = 908

  override def allHeaders: Map[String, Seq[String]] = ???

  override def statusText: String = "908"

  override def underlying[T]: T = ???

  override def xml: Elem = ???

  override def body: String = ???

  override def header(key: String): Option[String] = ???

  override def cookie(name: String): Option[WSCookie] = ???

  override def bodyAsBytes: Array[Byte] = ???

  override def cookies: Seq[WSCookie] = ???

  override def json: JsValue = ???
}

object Http {
  val config = new AsyncHttpClientConfigBean().setAcceptAnyCertificate(true)
  val client = new NingWSClient(config)

  //  val _url = "https://vs-docker.informatik.haw-hamburg.de/ports/8053/services"

  /*
   * Async get request
   */
  def get(_url: String): Future[WSResponse] = {
    client.url(_url).get()
  }

  /*
   * sync get request with timeout
   */
  def get(_url: String, timeout: Duration): WSResponse = {
    handle(client.url(_url).get(), timeout)
  }

  /*
   * Async post request
   */
  def post(_url: String): Future[WSResponse] = {
    client.url(_url).post("")
  }

  /*
   * sync post request with timeout
   */
  def post(_url: String, timeout: Duration): WSResponse = {
    handle(client.url(_url).post(""), timeout)
  }

  /*
   * Async post request
   */
  def post(_url: String, body: String): Future[WSResponse] = {
    client.url(_url).post(body)
  }

  /*
   * sync post request with timeout
   */
  def post(_url: String, body: String, timeout: Duration): WSResponse = {
    handle(client.url(_url).post(body), timeout)
  }

  /*
   * Async post request
   */
  def post(_url: String, params: Map[String, Seq[String]]): Future[WSResponse] = {
    client.url(_url).post(params)
  }

  /*
   * sync post request with timeout
   */
  def post(_url: String, params: Map[String, Seq[String]], timeout: Duration): WSResponse = {
    handle(client.url(_url).post(params), timeout)
  }

  /*
   * Async post request
   */
  //POST
  def put(_url: String): Future[WSResponse] = {
    client.url(_url).put("")
  }

  /*
   * sync put request with timeout
   */
  def put(_url: String, timeout: Duration): WSResponse = {
    handle(client.url(_url).put(""), timeout)
  }

  /*
   * Async put request
   */
  def put(_url: String, body: String): Future[WSResponse] = {
    client.url(_url).put(body)
  }

  /*
   * sync put request with timeout
   */
  def put(_url: String, body: String, timeout: Duration): WSResponse = {
    handle(client.url(_url).put(body), timeout)
  }

  /*
   * Async put request
  */
  def put(_url: String, params: Map[String, Seq[String]]): Future[WSResponse] = {
    client.url(_url).put(params)
  }

  /*
   * sync put request with timeout
   */
  def put(_url: String, params: Map[String, Seq[String]], timeout: Duration): WSResponse = {
    handle(client.url(_url).put(params), timeout)
  }

  //DELETE
  /*
   * Async DELETE request
   */
  def delete(_url: String): Future[WSResponse] = {
    client.url(_url).delete()
  }

  /*
   * sync delete request with timeout
   */
  def delete(_url: String, timeout: Duration): WSResponse = {
    handle(client.url(_url).delete(), timeout)
  }

  def handle(request: Future[WSResponse], timeout: Duration): WSResponse = {
    try {
      Await.result(request, timeout)
    } catch {
      case _: Throwable => Timeout()
    }
  }

  /*
   EXAMPLES

  //GET
    client.url(_url).get() map {
      response => println(response.body)
    }

  //POST
    client.url(_url).post(Map("key" -> Seq("value"))) map {
      response => println(response.body)
    }

    client.url(_url).post("someText") map {
      response => println(response.body)
    }

    //PUT
    client.url(_url).put("someText") map {
      response => println(response.body)
    }

    //DELETE
    client.url(_url).delete() map {
      response => println(response.body)
    }


    client.url("https://vs-docker.informatik.haw-hamburg.de/ports/8053/services").get() map (
      response => println(response.body)
      ) recover {
      case ex: Exception => println("ERROR ---" + ex.getMessage)
    }


    client.url("https://vs-docker.informatik.haw-hamburg.de/ports/8053/services").get() onComplete {
      case Success(response) => println("---" + response.body)
      case Failure(exception) => println("---" + exception.getMessage)
    }
   */
}