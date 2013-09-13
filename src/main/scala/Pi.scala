package com.hello
import akka.actor._
import akka.routing.RoundRobinRouter
import scala.math.random
import scala.concurrent.duration._

sealed trait PiMessage
case object Calculate extends PiMessage
case class Work(start:Int, nrOfElements:Int) extends PiMessage
case class Result(value:Double) extends PiMessage
case class Scream(foo:String) extends PiMessage
case class PiApproximation(pi:Double)

class Worker extends Actor {
  def receive = {
    case Work(start,nrOfElements)=>
      sender ! Result(calculatePiFor(start,nrOfElements))
      sender ! Scream("HELLO")
  }

  def calculatePiFor(start:Int,nrOfElements:Int): Double = {
    var acc = 0.0
    for(i <- start until (start + nrOfElements))
      acc += 4.0 * (1 - (i % 2) * 2) / (2 * i + 1)
    acc
  }
}

class Master(nrOfWorkers:Int, nrOfMessages:Int, nrOfElements:Int, listener:ActorRef) extends Actor {

  var pi: Double = _
  var nrOfResults: Int = _
  var start: Long = System.currentTimeMillis

  val workerRouter = context.actorOf(Props[Worker].withRouter(RoundRobinRouter(nrOfWorkers)), name="workerRouter")

  def receive = {
    case Calculate =>
      for(i <- 0 until nrOfMessages) workerRouter ! Work(i * nrOfElements, nrOfElements)
    case Scream(foo) =>
      //println(foo)
    case Result(value) =>
      pi += value
      nrOfResults += 1
      if(nrOfResults == nrOfMessages){
        listener ! PiApproximation(pi)
        context.stop(self)
      }
  }
}

class Listener extends Actor {
  def receive = {
    case PiApproximation(pi) =>
      println("\n\tPi approximation: \t\t%s\n\t".format(pi))
      context.system.shutdown()
  }
}

object Pi {
  def main(args:Array[String]){
    calculate(nrOfWorkers=4, nrOfElements=10000, nrOfMessages=1000)
  }

  def calculate(nrOfWorkers:Int, nrOfElements:Int, nrOfMessages:Int){
    val system = ActorSystem("PiSystem")
    val listener = system.actorOf(Props[Listener], name="Listener")
    val master = system.actorOf(Props(new Master(nrOfWorkers,nrOfMessages,nrOfElements,listener)), name="Master")
    master ! Calculate
  }
}

