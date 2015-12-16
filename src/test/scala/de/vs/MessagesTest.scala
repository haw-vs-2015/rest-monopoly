package de.vs

/**
 * Created by alex on 11.11.15.
 */

import de.alexholly.util.JettyServer
import de.alexholly.util.http.HttpSync._
import de.vs.monopoly.logic._
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods._
import org.json4s.native.Serialization.write
import org.scalatest._


class MessagesTest extends FunSuite with BeforeAndAfter {

  //JSON stuff
  implicit val jsonFormats = DefaultFormats

  //Debugging stuff
  val BODY_MESSAGE = " BODY EMPTY?"
  val JSON_MESSAGE = " JSON ERROR"
  val EMPTY_MESSAGE = " SHOULD BE EMPTY"
  val TIMEOUT = 10000

  //@TODO remove global stuff and if's from logic
  //@TODO Add service Manager and ask the ip/port
  var server = JettyServer().startOnFreePort()
  Global.default_url = "http://localhost:" + server.port
  Global.testMode = true
  var default_url = Global.default_url

  after {
    Messages.reset()
  }

  test("get all channels empty") {
    var response = get(default_url + "/messages", TIMEOUT)
    assert(response.status == 200)

    val obj = parse(response.body).extract[Channels]
    assert(obj.channels.size == 0)
  }

  test("get all channels one entry") {
    val subscriber = Subscriber("Alex", List[String](), "http://localhost:4567")
    var response = post(default_url + "/messages/subscribe/test", write(subscriber), TIMEOUT)
    assert(response.status == 200)

    response = get(default_url + "/messages", TIMEOUT)
    assert(response.status == 200)

    val obj = parse(response.body).extract[Channels]
    assert(obj.channels.size == 1)
  }

  test("add two subscriber to a channel and check if all are on the right channel") {
    //First subscriber
    val subscriber = Subscriber("Alex", List[String](), "http://localhost:4567")
    val subscriber3 = subscriber.copy(uri=subscriber.uri+"/messages/send/test")

    var response = post(default_url + "/messages/subscribe/test", write(subscriber), TIMEOUT)
    assert(response.status == 200)

    response = get(default_url + "/messages", TIMEOUT)
    assert(response.status == 200)

    var obj = parse(response.body).extract[Channels]
    assert(obj.channels.size == 1)
    assert(obj.channels(0).name == "test")
    assert(obj.channels(0).subscribers.size == 1)
    assert(obj.channels(0).subscribers(0) == subscriber3.id)

    //Second subscriber
    val subscriber2 = Subscriber("Mustermann", List[String](), "http://localhost:4567/mustermann")
    val subscriber4 = subscriber2.copy(uri=subscriber2.uri+"/messages/send/test")
    response = post(default_url + "/messages/subscribe/test", write(subscriber2), TIMEOUT)
    assert(response.status == 200)

    response = get(default_url + "/messages", TIMEOUT)
    assert(response.status == 200)

    obj = parse(response.body).extract[Channels]
    assert(obj.channels.size == 1)
    assert(obj.channels(0).name == "test")
    assert(obj.channels(0).subscribers.size == 2)
    assert(obj.channels(0).subscribers(1) == subscriber4.id)
  }

  test("remove last subscriber and check that the channel is beeing removed") {
    //First subscriber
    val subscriber = Subscriber("127.0.0.1", List[String](), "http://localhost:4567/alex")
    var response = post(default_url + "/messages/subscribe/test", write(subscriber), TIMEOUT)
    assert(response.status == 200)

    response = get(default_url + "/messages", TIMEOUT)
    assert(response.status == 200)

    var obj = parse(response.body).extract[Channels]
    assert(obj.channels.size == 1)

    response = delete(default_url + "/messages/test/" + subscriber.id, TIMEOUT)
    assert(response.status == 200)

    response = get(default_url + "/messages", TIMEOUT)
    assert(response.status == 200)

    obj = parse(response.body).extract[Channels]
    assert(obj.channels.size == 0)

  }

  test("send a Message to an empty non existed channel") {
    val message = Message("Roll", "board updated", "http://localhost:4567/alex", "Das Board")

    var response = post(default_url + "/messages/send/test", write(message), TIMEOUT)
    assert(response.status == 202)

    //First subscriber
    val subscriber = Subscriber("Alex", List[String](), "http://localhost:4567/alex")
    response = post(default_url + "/messages/subscribe/test", write(subscriber), TIMEOUT)
    assert(response.status == 200)

    response = post(default_url + "/messages/send/test", write(message), TIMEOUT)
    assert(response.status == 200)
  }

  //@TODO Tests fehlen zu den neuen funktionalitäten
  // - Keine doppelten ids
  // Channels mitgeben, denen gejoint werden soll
  // Liste aller subscribeten channels eines subscribers
  // Entfernen eines subscribers aus alles subscribten channels
  // usw.
  // Automatisches entfernen aus allen channels
}