package com.lightbend.akka.sample.ClientBroker

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

class RAutoTemperatura extends Actor{

  def receive: Receive={
    case ERROR => println("")
  }

}

object RAutoTemperatura{
  def main(args: Array[String]): Unit = {

    val config = ConfigFactory.parseString(conf)
    val client = ActorSystem("RemoteClient", config)
    val actor  = client.actorOf(Props[RAutoTemperatura], "AutoValGenTemp")

    val path =  "akka.tcp://server@127.0.0.1:2552/user/Broker"
    val Broker = client.actorSelection(path)

    Thread.sleep(1000)

    val R = org.ddahl.rscala.RClient()

    R eval
      """
         printf <- function(...) cat (sprintf(...))
         printf("[RClient]: This is an automated client\n")
         printf("[RClient]: Press any key to start\n")
          """

    scala.io.StdIn.readLine()
    while(true){

      R eval
        """

        x <- sample(1000:5000,1)
        printf("[RClient]: Random Instant: %d ms\n", x)

        """
      var random_access=R.x
      println(random_access._1)
      Broker ! DatoT(random_access._1.toString.toInt)
      Thread.sleep(1000)
      //Thread.sleep(random_access._1.toString.toInt)
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
