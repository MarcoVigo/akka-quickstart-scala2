package com.lightbend.akka.sample

import akka.actor.{Actor, ActorRef, ActorSystem, Props}


object Client1 extends App {
  case class Topic(a:Boolean, other:ActorRef)

  class Client1 extends Actor  {
    def receive = {
      case i: Int =>
        println(self+""+i)
      case s: String =>
        println(self+""+s)
    }
  }

  class Broker extends Actor{

    private val interi= collection.mutable.Buffer[ActorRef]()
    private val stringhe= collection.mutable.Buffer[ActorRef]()

    def receive = {
      case Topic(a,other) =>
        if(a) interi+=other
        else stringhe+=other
      case i: Int=>
        interi.foreach(_ ! i)
      case s: String =>
        stringhe.foreach(_ ! s)
    }
  }

  val system = ActorSystem("System")

  val actor1 = system.actorOf(Props[Client1],"Client1")
  val actor2 = system.actorOf(Props[Client1],"Client2")
  val actor = system.actorOf(Props[Broker],"Broker")

  actor ! Topic(a = true,actor1)
  actor ! Topic(a = true,actor2)
  actor ! Topic (a = false,actor2)
  actor ! Topic (a = false,actor1)
  actor ! 3
  actor ! "Ciao"


  Thread.sleep(1000)

  system.terminate()

}
