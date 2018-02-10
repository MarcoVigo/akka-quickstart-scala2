package com.lightbend.akka.sample.RaScala
import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

class RBroker extends Actor {
  val R = org.ddahl.rscala.RClient()
  var count=0

  def receive: Receive = {
    case s:String =>
      R eval
        """
         x=c()
         y=c()
         a=0
         b=0
      """
    case i:Int =>
      R.a=count
      R.b=i
      count=count+1
      R eval
        """
          x= append(x,a)
          y= append(y,b)
          plot(x,y,type ="b")

      """
  }
}

object RBroker {
  def main(args: Array[String]): Unit = {

    val config = ConfigFactory.parseString(conf)
    val server = ActorSystem("server", config)
    server.actorOf(Props[RBroker], "Broker")
    println("Connected")

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
