package com.example.unfilter

import akka.actor.ActorRef
import unfiltered.netty.websockets.WebSocket

object Message {
  case object Quit

  sealed trait ToiletAction

  case class Occupied(id: String) extends ToiletAction

  case class Vacant(id: String) extends ToiletAction

  case class Enquiry(id: String, asker: Option[ActorRef] = None) extends ToiletAction

  trait ClientAction

  case class Register(id: String, socket: WebSocket) extends ClientAction

  case class DeRegister(id: String, socket: WebSocket) extends ClientAction

}
