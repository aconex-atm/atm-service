package com.example.unfilter.actors

import akka.actor.Actor
import com.example.unfilter.Message.Quit
import com.example.unfilter.models.{Tid, ToiletSlot}
import com.example.unfilter.util.EventJsonConverter
import unfiltered.netty.websockets.WebSocket

class ToiletStatusSubscriber(tid: Tid, socket: WebSocket) extends Actor with EventJsonConverter {

  import context._

  override def receive: Receive = {
    case t: ToiletSlot => socket.send(toJson(t))
    case Quit => stop(self)
  }
}


