package com.lightbend.akka.sample.Remote

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

class RemoteActorR1 extends Actor with ActorLogging {
  override def receive: Receive = {
    case msg => log.info(s"Server Received $msg")
  }
}

  object RemoteActorR1 {
    def main(args: Array[String]): Unit = {
      val config = ConfigFactory.parseString(conf)
      val server = ActorSystem("remote-r1", config)
      server.actorOf(Props[RemoteActorR1], "echo")
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

