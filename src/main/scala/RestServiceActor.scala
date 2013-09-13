package com.hello.rest

import scala.concurrent.duration._
import akka.actor._
import akka.routing.RoundRobinRouter
import spray.http._
import spray.routing._
import spray.can._
import spray.util._
import HttpMethods._

case class ShoutOut(shout:String)
case class ShoutRequest

class StarScream extends Actor with RestService {

  implicit def actorRefFactory = context
  
  def receive = {
    case ShoutRequest =>
      sender ! ShoutOut("Foo!")
  }

}

class RestServiceActor extends Actor with RestService {
  implicit def actorRefFactory = context
  import context.dispatcher
  
  val workerRouter = context.actorOf(Props[StarScream].withRouter(RoundRobinRouter(4)), name="workerRouter")

  def receive = {
    case _: Http.Connected => sender ! Http.Register(self)
    case HttpRequest(GET, Uri.Path("/ping"), _, _, _) =>
      workerRouter ! ShoutRequest
      sender ! HttpResponse(entity = "PONG") 
    case HttpRequest(GET, Uri.Path("/stop"), _, _, _)=>
      sender ! HttpResponse(entity = "Shutting down in 1 second ...")
      context.system.scheduler.scheduleOnce(1.second) { context.system.shutdown() }
    case _: HttpRequest => sender ! HttpResponse(status = 404, entity = "Unknown resource!")
    case ShoutOut(shout) =>
      println(shout)
  }
}

trait RestService extends HttpService {

  implicit val executionContext = actorRefFactory.dispatcher
}
