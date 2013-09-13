package com.hello.rest

import akka.actor.Actor
import spray.http._
import spray.routing._
import spray.can._
import spray.util._
import HttpMethods._

class RestServiceActor extends Actor with RestService {
  implicit def actorRefFactory = context

  def receive = {
    case _: Http.Connected => sender ! Http.Register(self)
    case HttpRequest(GET, Uri.Path("/ping"), _, _, _) =>
      sender ! HttpResponse(entity = "PONG") 
  }
}

trait RestService extends HttpService {

  implicit val executionContext = actorRefFactory.dispatcher
}
