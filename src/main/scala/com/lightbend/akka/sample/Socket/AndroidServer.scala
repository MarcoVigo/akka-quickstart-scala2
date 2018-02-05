package com.lightbend.akka.sample.Socket

import java.io.{BufferedReader, InputStreamReader, PrintStream}
import java.net.{ServerSocket, Socket}
import java.util.concurrent.ConcurrentHashMap
import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AndroidServer extends App {
  case class User(name: Int, sock: Socket, in: BufferedReader, out: PrintStream)
  val users = new ConcurrentHashMap[Int, User]().asScala
  var count= 0
  println("HI")


  Future { checkConnections() }
  while(true){
    for((name, user) <- users){
      doChat(user)
    }
    Thread.sleep(200)
  }

  def checkConnections(): Unit = {
    val ss = new ServerSocket(4000)
    while(true){
      val sock = ss.accept()
      val in = new BufferedReader(new InputStreamReader(sock.getInputStream))
      val out = new PrintStream(sock.getOutputStream)
      Future{
        //out.println("What is your name")
        val name=  count
        val user= User(name, sock, in, out)
        users += name-> user
        count= count +1
      }
    }
  }

  def nonblockingRead(in: BufferedReader): Option[String] = {
    if(in.ready()) Some(in.readLine()) else None
  }

  def doChat(user: User): Unit = {
    nonblockingRead(user.in).foreach { input =>
      if(input == "quit"){
        user.sock.close()
        users -= user.name
        println("GONE :(")
      } else {
        //for((n, u) <- users){
          //u.out.println(user.name+" : "+input)
        //}
        println(input)
      }
    }

  }
}
