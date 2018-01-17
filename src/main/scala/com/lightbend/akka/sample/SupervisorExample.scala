package com.lightbend.akka.sample

import akka.actor.SupervisorStrategy._
import akka.actor.{Actor, ActorSystem, OneForOneStrategy, Props}


//Quando l'attore figlio "fallisce" manda un messaggio al "padre" che può decidere come gestire la cosa settando
// una Supervisor Strategy (OneForOneStrategy / All ForOne Strategy)

object SupervisorExample extends App {
  case object CreateChild
  case class SignalChildren(order: Int)
  case class PrintSignal(order: Int)
  case class DivideNumbers(n:Int, d:Int)
  case object BadStuff

  class ParentActor extends Actor{
    private var number =0

    def receive = {
      case CreateChild =>
        context.actorOf(Props[ChildActor],"child"+number)
        number +=1
      case SignalChildren(n) =>
        context.children.foreach(_ ! PrintSignal(n))
    }

    override val supervisorStrategy = OneForOneStrategy(loggingEnabled = false){
      case ae:ArithmeticException => Resume
      case _:Exception => Restart
    }
  }

  class ChildActor extends Actor{
    println("Child Created.")
    def receive = {
      case PrintSignal(n) => println(n+""+self)
      case DivideNumbers(n,d) => println(n/d)
      case BadStuff => throw new RuntimeException("Shit happens")
    }
    override def preStart()={ //si usa per acquisire le risorse che servono all'attore (es. database)
      super.preStart()
      println("preStart")
    }
    override def postStop()={ // si usa per rilasciare le risorse acquisite dall'attore in precedenza
      super.postStop()
      println("postStop")
    }
    override def preRestart(reason:Throwable, message:Option[Any])={
      super.preRestart(reason,message)
      println("preRestart")
    }
    override def postRestart(reason:Throwable)={
      super.postRestart(reason)
      println("postRestart")
    }
  }


  val system = ActorSystem("HierarchySystem")
  val actor = system.actorOf(Props[ParentActor],"Parent1")
  val actor2 = system.actorOf(Props[ParentActor],"Parent2")


  actor ! CreateChild
  //actor ! CreateChild

  val child0 = system.actorSelection("/user/Parent1/child0")

  child0 ! DivideNumbers(4,0)
  child0 ! DivideNumbers(4,2)
  child0 ! BadStuff

  Thread.sleep(1000)
  system.terminate()
}
