
import de.vs.rest.server.monopoly.app._
import org.scalatra._
import javax.servlet.ServletContext


class ScalatraBootstrap extends LifeCycle {

  override def init(context: ServletContext) {
    //@TODO Check Async bei multible servlets see http://www.smartjava.org/content/tutorial-getting-started-scala-and-scalatra-part-iv
    //ganz unten

    context mount (new DiceServlet(), "/dice/*")
    context mount (new BoardServlet(), "/boards/*")
    context mount (new GameServlet(), "/games/*")
    context.mount(new AsyncTestServlet(), "/test/*")
  }

}
