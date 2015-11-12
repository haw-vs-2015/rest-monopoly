import de.vs.http.client.Http
import de.vs.rest.server.monopoly.app._
import org.eclipse.jetty.servlet.ServletHandler
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra._
import javax.servlet.ServletContext


class ScalatraBootstrap extends LifeCycle {

  override def init(context: ServletContext) {
    context.mount(new MonopolyServlet(), "/*")
    //Http
  }

}
