package remote

import akka.actor.ActorRef

case class Aggiornamento(valore:Int, other:ActorRef, s:String)
case object ERROR
case object ERROR1
case object Stamp
case class Update(topic:Int,valore:Int,other:ActorRef)
case class Topic(i:Int, other:ActorRef)
case class Exit(other: ActorRef)