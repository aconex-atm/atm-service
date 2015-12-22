package com.example.unfilter

import akka.actor.{Props, ActorSystem}
import com.example.unfilter.repos.{ToiletRepository, NotifierRepository}

object Server extends App {

  val host: String = Env.of("HOST").getOrElse("127.0.0.1")

  val system = ActorSystem("system")

  val notifiers = system.actorOf(Props[NotifierRepository], "notifiers")
  val toilets = system.actorOf(Props[ToiletRepository], "toilets")

  val restApi = new RestApi(system, toilets, notifiers)
  val wsApi = new WSApi(system, notifiers)

  unfiltered.netty.Server.http(8080).plan(restApi).plan(wsApi).run
}

