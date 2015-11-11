package de.vs.http.client

/**
 * Created by alex on 10.11.15.
 */

import com.ning.http.client.{AsyncHttpClientConfigBean, AsyncHttpClientConfig}
import play.api.libs.ws.ning.{NingAsyncHttpClientConfigBuilder, NingWSClient}
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global


object MonopolyRESTClient {
  val config = new AsyncHttpClientConfigBean().setAcceptAnyCertificate(true)
  val client = new NingWSClient(config)
  val _url = "https://vs-docker.informatik.haw-hamburg.de/ports/8053/services"

  //1.
  //GET
  client.url(_url).get() map {
    response => println(response.body)
  }
/*
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
*/
  //2.
  //  client.url("https://vs-docker.informatik.haw-hamburg.de/ports/8053/services").get() map (
  //    response => println(response.body)
  //    ) recover {
  //    case ex: Exception => println("ERROR ---" + ex.getMessage)
  //  }

  //3.
  //    client.url("https://vs-docker.informatik.haw-hamburg.de/ports/8053/services").get() onComplete {
  //      case Success(response) => println("---" + response.body)
  //      case Failure(exception) => println("---" + exception.getMessage)
  //    }
}