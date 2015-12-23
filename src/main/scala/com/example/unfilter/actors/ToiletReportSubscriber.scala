package com.example.unfilter.actors

import akka.actor.{ActorRef, Actor}
import com.example.unfilter.Message._
import com.example.unfilter.models.{Tid, UsageStat}
import com.example.unfilter.util.UsageStatJsonConverter
import unfiltered.netty.websockets.WebSocket

import scala.util.{Failure, Success}

class ToiletReportSubscriber(tid: Tid, socket: WebSocket) extends Actor with UsageStatJsonConverter {

  import context._

  override def receive: Receive = {
    case usages: List[UsageStat] => socket.send(toJson(usages))
    case Quit => stop(self)
  }
}


