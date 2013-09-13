import akka.actor.{Props,ActorSystem}
import akka.io.IO
import spray.can.Http
import com.hello.rest.RestServiceActor

object Boot extends App {

  implicit val system = ActorSystem()

  val restService = system.actorOf(Props[RestServiceActor])

  IO(Http) ! Http.Bind(restService,interface = "localhost",port = 8080)

}
