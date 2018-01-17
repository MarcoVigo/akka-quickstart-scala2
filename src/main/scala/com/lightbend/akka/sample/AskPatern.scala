package com.lightbend.akka.sample


import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.pattern._
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success



object AskPatern extends App {
  case object AskName //non ha parametri pertanto non è class ma object, le classi devono essere istanziate
  case class NameResponse(val name: String)
  case class AskNameOf (other: ActorRef)

  implicit val timeout = Timeout(1.seconds)


  class AskActor(val name: String) extends Actor{
    implicit val ec = context.system.dispatcher

    def receive = {
      case AskName =>
        //Thread.sleep(10000) il sistema termina prima che l'attore abbia fatto il suo compito e quindi avremo errore
        sender ! NameResponse(name)

      case AskNameOf(other) =>
        val f = other ? AskName
        f.onComplete{
          case Success(NameResponse(n)) =>
            println("They said their name was "+n)
          case Success(s) =>
            println("They didn't tell us their name.")
          case Failure(ex) =>
            println("Asking their name failed.")
        }
        val currentSender = sender
        Future{
          currentSender ! "message"
        }
    }
  }

  val system = ActorSystem("SimpleSystem")
  val actor = system.actorOf(Props(new AskActor("Pat")),"AskActor1")
  val actor2= system.actorOf(Props(new AskActor("Val")),"AskActor2")

  //implicit val timeout = Timeout(1.seconds)

  val answer = actor ? AskName

  actor ! AskNameOf(actor2)

  answer.foreach(n => println("Name is "+n))

  system.terminate()


}
