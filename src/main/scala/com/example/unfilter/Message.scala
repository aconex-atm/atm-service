package com.example.unfilter

import java.time.Duration

import akka.actor.ActorRef
import com.example.unfilter.models.Tid
import org.joda.time.{DateTime, DateTimeZone}
import unfiltered.netty.websockets.WebSocket


object Message {

  case object Quit

  sealed trait ToiletEvent {
    def id: Tid

    def time: DateTime

    def name: String
  }

  case class Occupied(id: Tid, time: DateTime = localNow) extends ToiletEvent {
    def name = "Occupied"
  }

  def localNow: DateTime = {
    DateTime.now(DateTimeZone.forID("+11"))
  }

  case class Vacant(id: Tid, time: DateTime = localNow) extends ToiletEvent {
    def name = "Vacant"
  }

  case class Enquiry(id: Tid, time: DateTime = localNow, asker: Option[ActorRef] = None) extends ToiletEvent {
    def name = "Enquiry"
  }


  trait ClientAction

  case class Register(id: Tid, socket: WebSocket) extends ClientAction

  case class DeRegister(id: Tid, socket: WebSocket) extends ClientAction

  case class RegisterForReport(id: Tid, socket: WebSocket) extends ClientAction

  case class Usage(id: Tid, duration: Duration)

}




