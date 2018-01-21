package com.lightbend.akka.sample.remote

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import remote._

class Client extends Actor with ActorLogging {

  var LastPressione=0
  var LastTemperatura=0

  def receive: Receive = {

    case Aggiornamento(valore,other,s)=>
      println(""+s+" aggiornata correttamente da "+other)
      println ("Nuovo valore = " +valore)
      if(s.equals("Temperatura")) LastTemperatura=valore
      else if(s.equals("Pressione")) LastPressione=valore

    case s:String =>
      println(""+s)

    case ERROR =>
      println("ERRORE TOPIC INESISTENTE")

  }
}

object Client {

  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.parseString(conf)
    val client = ActorSystem("RemoteClient", config)
    val actor  = client.actorOf(Props[Client], "Client")

    val path =  "akka.tcp://server@0.0.0.0:2552/user/Broker"
    val Broker = client.actorSelection(path)


    println("Selezionare il topic di sottoscrizione: ")
    println("1) Temperatura ")
    println("2) Pressione")
    println("3) ALL_TOPIC")
    val x = scala.io.StdIn.readInt()
    Broker ! Topic(x,actor)


    while(true){
      println("Selezionare il topic da aggiornare")
      println("1) Temperatura ")
      println("2) Pressione")
      val y = scala.io.StdIn.readInt()

      if(y==1) {
        println("Inserire aggiornamento Temperatura")
        val a= scala.io.StdIn.readInt()
        Broker ! Update(1,a,actor)
      }
      if(y==2) {
        println("Inserire aggiornamento Pressione")
        val a= scala.io.StdIn.readInt()
        Broker ! Update(2,a,actor)
      }

      Thread.sleep(10000)
    }


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
      |      port = 0
      |    }
      |  }
      |}
    """.stripMargin

}