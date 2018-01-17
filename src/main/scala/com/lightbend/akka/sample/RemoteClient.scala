package com.lightbend.akka.sample

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

class RemoteClient extends Actor with ActorLogging {
  override def receive: Receive = {
    case msg => log.info(s"Server Received $msg")
  }
}

object RemoteClient{
  case class Update(s:Int,a:Int,other:ActorRef)
  case class Topic(i:Int, other:ActorRef)

  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.parseString(conf)
    val client = ActorSystem("remote1", config)
    val actor  = client.actorOf(Props[RemoteClient], "Client1")

    val path =  "akka.tcp://remote-r1@0.0.0.0:2552/user/Broker"
    val Broker = client.actorSelection(path)

    //Broker ! "Hello Server"

    println("Selezionare il topic di sottoscrizione: ")
    println("1) Temperatura ")
    println("2) Pressione")
    println("3) ALL_TOPIC")
    val x = scala.io.StdIn.readInt()
    Broker ! Topic(x,actor)
    println(x)

    while(true){
      println("Selezionare il topic da aggiornare")
      println("1) Temperatura ")
      println("2) Pressione")
      val y = scala.io.StdIn.readInt()
      println(y)
      if(y==1) {
        println("Inserire aggiornamento Temperatura")
        val a= scala.io.StdIn.readInt()
        //Broker ! Update(1,a,actor)
        println(a)
      }
      if(y==2) {
        println("Inserire aggiornamento Pressione")
        val a= scala.io.StdIn.readInt()
        //Broker ! Update(2,a,actor)
        println(a)
      }
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
  |      port = 2551
  |    }
  |  }
  |}
""".stripMargin

}
