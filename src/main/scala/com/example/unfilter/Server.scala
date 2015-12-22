package com.example.unfilter

import akka.actor.{Props, ActorSystem}
import com.example.unfilter.repos.NotifierRepository

object Server extends App {

  val host: String = Env.of("HOST").getOrElse("127.0.0.1")

  val system = ActorSystem("system")
  val notifierRepository = system.actorOf(Props[NotifierRepository])

  val restApi = new ATMApi(system, notifierRepository)
  val wsApi = new AtmWSApi(system, notifierRepository)

  unfiltered.netty.Server.http(8080).plan(restApi).plan(wsApi).run
}

