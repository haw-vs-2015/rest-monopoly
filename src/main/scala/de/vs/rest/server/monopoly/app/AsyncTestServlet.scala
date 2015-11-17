package de.vs.rest.server.monopoly.app

import org.json4s._
import org.scalatra._
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.scalate.ScalateSupport

object AsyncTestServlet {

  var numbers = List[Int]()

  def reset(): Unit = {
    numbers = List[Int]()
  }
}

class AsyncTestServlet extends ScalatraServlet with ScalateSupport with JacksonJsonSupport {

  protected implicit val jsonFormats: Formats = DefaultFormats

  protected override def transformRequestBody(body: JValue): JValue = body.camelizeKeys

  before() {
    contentType = formats("json")
    response.headers += ("Access-Control-Allow-Origin" -> "*")
  }


  //get numbers
  get("/numbers") {
    Numbers(AsyncTestServlet.numbers)
  }

  //
  put("/async") {
    AsyncTestServlet.numbers :+= params("number").toInt
//    println("added" + params("number").toInt)
//    println(AsyncTestServlet.numbers)
  }

  //
  put("/sync") {
    AsyncTestServlet.numbers :+= params("number").toInt
//    println("added" + params("number").toInt)
//    println(AsyncTestServlet.numbers)
  }

}

case class Numbers(numbers: List[Int])
