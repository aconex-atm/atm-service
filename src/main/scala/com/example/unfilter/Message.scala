package com.example.unfilter

import java.time.Duration

import akka.actor.ActorRef
import com.example.unfilter.models.Tid
import org.joda.time.DateTime
import unfiltered.netty.websockets.WebSocket


object Message {

  case object Quit

  sealed trait ToiletEvent {
    def id: Tid

    def time: DateTime

    def name: String
  }

  case class Occupied(id: Tid, time: DateTime = DateTime.now) extends ToiletEvent {
    def name = "Occupied"
  }

  case class Vacant(id: Tid, time: DateTime = DateTime.now) extends ToiletEvent {
    def name = "Vacant"
  }

  case class Enquiry(id: Tid, time: DateTime = DateTime.now, asker: Option[ActorRef] = None) extends ToiletEvent {
    def name = "Enquiry"
  }


  trait ClientAction

  case class Register(id: Tid, socket: WebSocket) extends ClientAction

  case class DeRegister(id: Tid, socket: WebSocket) extends ClientAction

  case class RegisterForReport(id: Tid, socket: WebSocket) extends ClientAction

  case class Usage(id: Tid, duration: Duration)

}




