package com.lightbend.akka.sample.remote

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSelection, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import remote._

class  Broker extends Actor with ActorLogging{


  var Temperatura=0
  var Pressione=0
  private val Topic_Temperatura= collection.mutable.Buffer[ActorRef]()
  private val Topic_Pressione= collection.mutable.Buffer[ActorRef]()


  def receive: Receive = {


    case Topic(i,other) =>
      println("SOTTOSCRIZIONE AL TOPIC RICEVUTA DA  "+other)
      if(i==1) Topic_Temperatura+=other
      else if(i==2) Topic_Pressione+=other
      else if(i==3){
        Topic_Temperatura+=other
        Topic_Pressione+=other
      }
      else other ! ERROR

    case Update(topic,valore,other)=>
      println("UPDATE RICEVUTO "+topic+""+valore+""+other)
      if(topic==1){
        Temperatura=valore
        Topic_Temperatura.foreach(_ ! Aggiornamento(valore,other,"Temperatura"))
      }
      else if(topic==2){
        Pressione=valore
        Topic_Pressione.foreach(_ ! Aggiornamento(valore,other,"Pressione"))
      }
      else other ! ERROR

  }
}

object Broker{
  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.parseString(conf)
    val server = ActorSystem("server", config)
    server.actorOf(Props[Broker], "Broker")
  }


  val conf =
    """
      |akka {
      |  actor {
      |    provider = "akka.remote.RemoteActorRefProvider"
      |  }
      |
      |  remote {
      |    enabled-transports = ["akka.remote.netty.tcp"]
      |    netty.tcp {
      |      hostname = "0.0.0.0"
      |      port = 2552
      |    }
      |  }
      |}
    """.stripMargin
}
