import de.vs.http.client.MonopolyRESTClient
import de.vs.rest.server.monopoly.app._
import org.scalatra._
import javax.servlet.ServletContext
import scala.concurrent.ExecutionContext.Implicits.global
class ScalatraBootstrap extends LifeCycle {

  override def init(context: ServletContext) {
    context.mount(new MonopolyServlet, "/*")
    MonopolyRESTClient
  }
}
