package de.vs.http.client

/**
 * Created by alex on 10.11.15.
 */

import dispatch._, Defaults._
import org.apache.http.client.methods.HttpOptions

import scala.util.{Failure, Success}

object MonopolyRESTClient {
//  val svc = url("http://www.google.de")

  //  get("https://vs-docker.informatik.haw-hamburg.de/ports/8053/services").map { response =>
  //    println(response)
  //  }

//  get("http://www.google.de").map { response =>
//    println(response)
//    println("aaaa")
//  }
//
//  def getServices() = {
//    //    val a = Http(url(_url) OK as.String)
//    //    a
//    val svc = url("https://vs-docker.informatik.haw-hamburg.de/ports/8053/services")
//    val country = Http.configure(_.setSSLContext({
//      val ctx = javax.net.ssl.SSLContext.getInstance("TLS")
//      ctx.init(null, null, null)
//      ctx
//    }))(svc OK as.String)
//
//    country onComplete {
//      case Success(response) =>
//        println(response)
//        if (response.status == 200) {
//
//
//
//          try {
//
//
//          } catch {
//            case e: Throwable =>
//
//          }
//        } else {
//
//        }
//      case Failure(exception) => println(exception)
//    }
//  }
}