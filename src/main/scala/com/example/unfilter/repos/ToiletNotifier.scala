package com.example.unfilter.repos

import akka.actor.Actor
import com.example.unfilter.Message.{Occupied, Quit, Vacant}
import com.example.unfilter.models.{Tid, ToiletSlot}
import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.native.JsonMethods._
import unfiltered.netty.websockets.WebSocket

class ToiletNotifier(tid: Tid, socket: WebSocket) extends Actor {

  import context._

  override def receive: Receive = {
    case Vacant(id) if id == tid => socket.send(pretty(("occupied" -> false)))
    case Occupied(id) if id == tid => socket.send(pretty(("occupied" -> true)))
    case t: ToiletSlot => socket.send(pretty(("occupied" -> t.occupied)))
    case Quit => stop(self)
  }

  def pretty(jValue: JValue): String = compact(render(jValue))
}


