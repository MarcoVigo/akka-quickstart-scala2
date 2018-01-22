package com.lightbend.akka.sample.ClientBroker

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
      if(i==1 && !Topic_Temperatura.contains(other)) Topic_Temperatura+=other
      else if(i==2 && !Topic_Pressione.contains(other)) Topic_Pressione+=other
      else if(i==3){
        if(!Topic_Temperatura.contains(other))Topic_Temperatura+=other
        if(!Topic_Pressione.contains(other))Topic_Pressione+=other
      }
      else other ! ERROR

    case Update(topic,valore,other)=>
      println("UPDATE RICEVUTO "+topic+""+valore+""+other)
      if(topic==1){
        if(Topic_Temperatura.contains(other)) {
          Temperatura = valore
          Topic_Temperatura.foreach(_ ! Aggiornamento(valore, other, "Temperatura"))
        }
        else other ! ERROR1
      }
      else if(topic==2) {
        if (Topic_Pressione.contains(other)) {
          Pressione = valore
          Topic_Pressione.foreach(_ ! Aggiornamento(valore, other, "Pressione"))
        }
        else other ! ERROR1
      }
      else other ! ERROR

    case Exit(other)=>
      if(Topic_Pressione.contains(other)) Topic_Pressione-=other
      else other ! ERROR1
      if(Topic_Temperatura.contains(other)) Topic_Temperatura-=other
      else other ! ERROR1

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
      |      hostname = "127.0.0.1"
      |      port = 2552
      |    }
      |  }
      |}
    """.stripMargin
}
