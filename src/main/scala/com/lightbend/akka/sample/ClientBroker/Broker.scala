package com.lightbend.akka.sample.ClientBroker

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSelection, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

class  Broker extends Actor /*with ActorLogging*/{


  private val Topic_Temperatura= collection.mutable.Buffer[ActorRef]()
  private val Topic_Pressione= collection.mutable.Buffer[ActorRef]()


  def receive: Receive = {

    case Topic(i,other) =>
      println("SOTTOSCRIZIONE AL TOPIC RICEVUTA DA  "+other)
      if(i==1 && !Topic_Temperatura.contains(other)) {
        Topic_Temperatura += other
        other ! SottoscrizioneT
      }
      else if(i==2 && !Topic_Pressione.contains(other)){
        Topic_Pressione+=other
        other ! SottoscrizioneP
      }
      else if(i==3){
        if(!Topic_Temperatura.contains(other))Topic_Temperatura+=other
        if(!Topic_Pressione.contains(other))Topic_Pressione+=other
      }
      else other ! ERROR

    case DatoT(i) =>
      Topic_Temperatura.foreach(_ ! DatoT(i))

    case DatoP(i) =>
      Topic_Pressione.foreach(_ ! DatoP(i))

    case ExitT(other)=>
      if(Topic_Temperatura.contains(other)) Topic_Temperatura-=other
      else other ! ERROR1

    case ExitP(other)=>
      if(Topic_Pressione.contains(other)) Topic_Pressione-=other
      else other ! ERROR1


  }
}

object Broker{
  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.parseString(conf)
    val server = ActorSystem("server", config)
    server.actorOf(Props[Broker], "Broker")
    println("Broker Connesso")
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
      |      hostname = "127.0.0.1"
      |      port = 2552
      |    }
      |  }
      |}
    """.stripMargin
}
