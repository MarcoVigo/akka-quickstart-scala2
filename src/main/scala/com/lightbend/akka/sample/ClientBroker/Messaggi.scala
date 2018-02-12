package com.lightbend.akka.sample.ClientBroker
import akka.actor.ActorRef

/*case class Aggiornamento(valore:Int, other:ActorRef, s:String)


case object Stamp
case class Update(topic:Int,valore:Int,other:ActorRef)*/


case object ERROR1
case object ERROR
case class ExitT(other: ActorRef)
case class ExitP(other: ActorRef)
case class Topic(i:Int, other:ActorRef)
case object SottoscrizioneT
case object SottoscrizioneP
case class DatoT(i: Int)
case class DatoP(i: Int)
