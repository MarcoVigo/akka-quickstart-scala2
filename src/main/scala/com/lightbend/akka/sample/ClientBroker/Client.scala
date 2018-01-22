package com.lightbend.akka.sample.ClientBroker

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
    case ERROR1 =>
      println("ERRORE NON SEI ISCRITTO AL TOPIC SELEZIONATO")

    case Stamp =>
      println("Ultima Temperatura: "+ LastTemperatura)
      println("Ultima Pressione: "+ LastPressione)

  }
}

object Client {

  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.parseString(conf)
    val client = ActorSystem("RemoteClient", config)
    val actor  = client.actorOf(Props[Client], "Client")

    val path =  "akka.tcp://server@127.0.0.1:2552/user/Broker"
    val Broker = client.actorSelection(path)





    while(true){
      println("0) Sottoscrizione a un Topic")
      println("1) Aggiorna Temperatura ")
      println("2) Aggiorna Pressione")
      println("3) Ultimo valore")
      println("4) Lascia Topic")
      val y = scala.io.StdIn.readInt()

      if(y==0){
        println("Selezionare il topic di sottoscrizione: ")
        println("1) Temperatura ")
        println("2) Pressione")
        println("3) ALL_TOPIC")
        val x = scala.io.StdIn.readInt()
        Broker ! Topic(x,actor)
      }
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
      if(y==3) actor ! Stamp
      if(y==4) Broker ! Exit(actor)

      Thread.sleep(2500)
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