package com.example.unfilter.repos

import akka.actor.Actor
import com.example.unfilter.repos.ToiletRepository.{Occupied, Vacant}
import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.native.JsonMethods._
import unfiltered.netty.websockets.WebSocket

class ToiletNotifier(tid: String, socket: WebSocket) extends Actor {
  override def receive: Receive = {
    case Vacant(id) if id == tid => socket.send(pretty( ("id" -> tid) ~ ("occupied" -> false)))
    case Occupied(id) if id == tid => socket.send(pretty( ("id" -> tid) ~ ("occupied" -> true)))
  }

  def pretty(jValue: JValue): String = compact(render(jValue))
}
