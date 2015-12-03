package de.vs.rest.server.monopoly.app

import org.json4s._
import org.scalatra._
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.scalate.ScalateSupport

class ServicesServlet extends ScalatraServlet with ScalateSupport with JacksonJsonSupport {

  protected implicit val jsonFormats: Formats = DefaultFormats

  protected override def transformRequestBody(body: JValue): JValue = body.camelizeKeys

  before() {
    contentType = formats("json")
    response.headers += ("Access-Control-Allow-Origin" -> "*")
  }

  //List of available service
  get("/") {

  }

  //creates a new service
  post("/") {

  }

  //Gets a services
  get("/services/:id") {

  }

  //places a services
  put("/services/:id") {

  }

  //places a services
  delete("/services/:id") {

  }

  //@TODO anderen Link ausdenken
  //List of available of name
  get("/services/of/name/:name") {

  }

  //List of available of type
  get("/services/of/type/:name") {

  }
}