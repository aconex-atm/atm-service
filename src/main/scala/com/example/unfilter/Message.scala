package com.example.unfilter

import java.time.Duration
import java.util.Date

import akka.actor.ActorRef
import com.example.unfilter.models.{ToiletSlot, Tid}
import org.joda.time.{DateTime, DateTimeZone}
import unfiltered.netty.websockets.WebSocket


object Message {

  case object Quit


  def localNow: Date = {
    DateTime.now(DateTimeZone.forID("+11")).toDate
  }


  case class Enquiry(id: Tid, time: Date = localNow, asker: Option[ActorRef] = None) {
    def name = "Enquiry"
  }


  trait ClientAction

  case class Subscribe(id: Tid, socket: WebSocket) extends ClientAction

  case class Unsubscribe(id: Tid, socket: WebSocket) extends ClientAction

  case class RegisterForReport(id: Tid, socket: WebSocket) extends ClientAction

  case class Updated(slot: ToiletSlot) extends ClientAction

  case class Usage(id: Tid, duration: Duration)

}




