package com.example.unfilter

import akka.actor.ActorRef
import com.example.unfilter.models.Tid
import unfiltered.netty.websockets.WebSocket


object Message {

  case object Quit

  sealed trait ToiletAction

  case class Occupied(id: Tid) extends ToiletAction

  case class Vacant(id: Tid) extends ToiletAction

  case class Enquiry(id: Tid, asker: Option[ActorRef] = None) extends ToiletAction

  trait ClientAction

  case class Register(id: Tid, socket: WebSocket) extends ClientAction

  case class DeRegister(id: Tid, socket: WebSocket) extends ClientAction

}
