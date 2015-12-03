//package de.vs.rest.server.monopoly.app
//
//import de.vs.monopoly._
//import org.json4s._
//import org.scalatra._
//import org.scalatra.json.JacksonJsonSupport
//import org.scalatra.scalate.ScalateSupport
//
//class EventServlet extends ScalatraServlet with ScalateSupport with JacksonJsonSupport {
//
//  protected implicit val jsonFormats: Formats = DefaultFormats
//
//  protected override def transformRequestBody(body: JValue): JValue = body.camelizeKeys
//
//  before() {
//    contentType = formats("json")
//    response.headers += ("Access-Control-Allow-Origin" -> "*")
//  }
//
//  //Events
//
//  //List of available event
//  get("/:gameid") {
//
//  }
//
//  post("/:gameid") {
//    val event = parsedBody.extract[Event]
//    Ok(Event.add(params {
//      "gameid"
//    }))
//  }
//
//  delete("/:eventid") {
//
//  }
//
//  //gets the event details
//  get("/:eventid") {
//
//  }
//
//  //List of available subscription
//  get("/subscriptions ") {
//
//  }
//
//  post("/subscriptions ") {
//
//  }
//
//  //removes the subscription from the service
//  delete("/subscriptions/subscriptions/:subscription") {
//
//  }
//}
