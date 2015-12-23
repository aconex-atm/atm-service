package com.example.unfilter

import akka.actor.{Props, ActorSystem}
import com.example.unfilter.repos.{ReportSubscriberRepository, ToiletRepository, StatusSubscriberRepository}

object Server extends App {

  val host: String = Env.of("HOST").getOrElse("127.0.0.1")

  val system = ActorSystem("system")

  val statusSubscriberRepository = system.actorOf(Props[StatusSubscriberRepository], "status-subscribers")
  val reportSubscriberRepository = system.actorOf(Props[ReportSubscriberRepository], "report-subscribers")

  val toiletRepository = system.actorOf(Props[ToiletRepository], "toilets")

  val restApi = new RestApi(system, toiletRepository, List(statusSubscriberRepository, reportSubscriberRepository))
  val wsApi = new WSApi(system, statusSubscriberRepository, reportSubscriberRepository)

  unfiltered.netty.Server.http(8080).plan(restApi).plan(wsApi).run
}

