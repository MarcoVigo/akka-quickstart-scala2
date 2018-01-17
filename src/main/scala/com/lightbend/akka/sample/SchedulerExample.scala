package com.lightbend.akka.sample

import akka.actor.{Actor, ActorSystem, Props}
import scala.concurrent.duration._

object SchedulerExample extends App {
  case object Count
  class ScheduleActor extends Actor{
    var n=0;
    def receive = {
      case Count =>
        n+=1
        println(n)

    }

  }

  val system = ActorSystem("SimpleSystem")
  val actor = system.actorOf(Props[ScheduleActor],"Actor")
  implicit val ec= system.dispatcher

  actor ! Count

  system.scheduler.scheduleOnce(1.second)(actor ! Count)

  val can=system.scheduler.schedule(0.second, 100.millis, actor, Count)
  //system.scheduler.schedule(0.second, 100.millis)( actor ! Count) //2 modi diversi di scrivere la stessa cosa

  Thread.sleep(2000) //se non lo metto stampo solo 2 volte perchè chiamo system.terminate() prima degli altri Count
  can.cancel() // così facendo evito errori che potrebbero verificarsi se termino il sistema prima della fine dei vari schedule
  system.terminate()
}
