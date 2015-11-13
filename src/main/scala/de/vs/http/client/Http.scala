package de.vs.http.client

/**
 * Created by alex on 10.11.15.
 */

import com.ning.http.client.AsyncHttpClientConfigBean
import play.api.libs.ws.WSResponse
import play.api.libs.ws.ning.NingWSClient
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global


object Http {
  val config = new AsyncHttpClientConfigBean().setAcceptAnyCertificate(true)
  val client = new NingWSClient(config)
  //  val _url = "https://vs-docker.informatik.haw-hamburg.de/ports/8053/services"
  val default_url = "http://localhost:4567"

  /*
   * Async get request
   */
  def get(_url: String): Future[WSResponse] = {
    client.url(default_url + _url).get()
  }

  /*
   * sync get request with timeout
   */
  def get(_url: String, timeout: Duration): WSResponse = {
    Await.result(client.url(default_url + _url).get(), timeout)
  }

  /*
   * Async post request
   */
  def post(_url: String): Future[WSResponse] = {
    client.url(default_url + _url).post("")
  }

  /*
   * sync post request with timeout
   */
  def post(_url: String, timeout: Duration): WSResponse = {
    Await.result(client.url(default_url + _url).post(""), timeout)
  }

  /*
   * Async post request
   */
  def post(_url: String, body: String): Future[WSResponse] = {
    client.url(default_url + _url).post(body)
  }

  /*
   * sync post request with timeout
   */
  def post(_url: String, body: String, timeout: Duration): WSResponse = {
    Await.result(client.url(default_url + _url).post(body), timeout)
  }

  /*
   * Async post request
   */
  def post(_url: String, params: Map[String, Seq[String]]): Future[WSResponse] = {
    client.url(default_url + _url).post(params)
  }

  /*
   * sync post request with timeout
   */
  def post(_url: String, params: Map[String, Seq[String]], timeout: Duration): WSResponse = {
    Await.result(client.url(default_url + _url).post(params), timeout)
  }

  /*
   * Async post request
   */
  //POST
  def put(_url: String): Future[WSResponse] = {
    client.url(default_url + _url).put("")
  }

  /*
   * sync put request with timeout
   */
  def put(_url: String, timeout: Duration): WSResponse = {
    Await.result(client.url(default_url + _url).put(""), timeout)
  }

  /*
   * Async put request
   */
  def put(_url: String, body: String): Future[WSResponse] = {
    client.url(default_url + _url).put(body)
  }

  /*
   * sync put request with timeout
   */
  def put(_url: String, body: String, timeout: Duration): WSResponse = {
    Await.result(client.url(default_url + _url).put(body), timeout)
  }

  /*
   * Async put request
  */
  def put(_url: String, params: Map[String, Seq[String]]): Future[WSResponse] = {
    client.url(default_url + _url).put(params)
  }

  /*
   * sync put request with timeout
   */
  def put(_url: String, params: Map[String, Seq[String]], timeout: Duration): WSResponse = {
    Await.result(client.url(default_url + _url).put(params), timeout)
  }

  //DELETE
  /*
   * Async DELETE request
   */
  def delete(_url: String): Future[WSResponse] = {
    client.url(default_url + _url).delete()
  }

  /*
   * sync delete request with timeout
   */
  def delete(_url: String, timeout: Duration): WSResponse = {
    Await.result(client.url(default_url + _url).delete(), timeout)
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