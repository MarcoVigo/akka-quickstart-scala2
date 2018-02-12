package com.lightbend.akka.sample.ClientBroker

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory



class  Client extends Actor {

  val R = org.ddahl.rscala.RClient()
  var countT=0
  var countP=0

  def receive: Receive = {
    case ERROR =>
      println("ERRORE TOPIC INESISTENTE")
    case ERROR1 =>
      println("ERRORE NON SEI ISCRITTO AL TOPIC SELEZIONATO")
    case SottoscrizioneT =>
      R eval
    """
         CampioneT=c()
         Temperatura=c()
         ContatoreT=0
         ValoreT=0
      """
    case SottoscrizioneP =>
      R eval
        """
         CampioneP=c()
         Pressione=c()
         ContatoreP=0
         ValoreP=0
      """
    case DatoT(i) =>
      R.ContatoreT=countT
      R.ValoreT=i
      countT+=1
      R eval
        """
          CampioneT= append(CampioneT,ContatoreT)
          Temperatura= append(Temperatura,ValoreT)
          plot(CampioneT,Temperatura,type ="b")

      """
    case DatoP(i) =>
      R.ContatoreP=countP
      R.ValoreP=i
      countP+=1
      R eval
        """
          CampioneP= append(CampioneP,ContatoreP)
          Pressione= append(Pressione,ValoreP)
          plot(CampioneP,Pressione,type ="b")

      """


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
      println("1) Sottoscrizione Temperatura")
      println("2) Sottoscrizione Pressione ")
      println("3) Sottoscrizione a Entrambi ")
      println("4) Lascia Topic Temperatura")
      println("5) Lascia Topic Pressione")
      val y = scala.io.StdIn.readInt()
      if(y==1 || y==2 || y==3 )
      Broker ! Topic(y,actor)
      if(y==4) Broker ! ExitT(actor)
      if(y==5) Broker ! ExitP(actor)
      else
      Broker ! ERROR
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