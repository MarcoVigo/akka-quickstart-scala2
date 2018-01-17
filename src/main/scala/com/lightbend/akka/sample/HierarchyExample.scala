package com.lightbend.akka.sample

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object HierarchyExample extends App {

  case object CreateChild
  //case object SignalChildren
  //case object PrintSignal

  case class SignalChildren(order: Int)  //serve per garantire l'ordine di arrivo e quindi di stampa a video dei messaggi
  case class PrintSignal(order: Int)

  class ParentActor extends Actor{
    private var number =0
    //private val children= collection.mutable.Buffer[ActorRef]()

    def receive = {
      case CreateChild =>
        //children+=
        context.actorOf(Props[ChildActor],"child"+number)
        number +=1
      case SignalChildren(n) =>
        context.children.foreach(_ ! PrintSignal(n))
    }
  }

  class ChildActor extends Actor{
    def receive = {
      case PrintSignal(n) => println(n+""+self)
    }
  }


  val system = ActorSystem("HierarchySystem")
  val actor = system.actorOf(Props[ParentActor],"Parent1")
  val actor2 = system.actorOf(Props[ParentActor],"Parent2")

  actor ! CreateChild
  actor ! SignalChildren(1)
  actor ! CreateChild
  actor ! CreateChild
  actor ! SignalChildren(2)

  actor2 ! CreateChild
  val child0= system.actorSelection("akka://HierarchySystem/user/Parent2/child0")
  child0 ! PrintSignal(3)

  Thread.sleep(1000)
  system.terminate()

}
