package com.example.unfilter

import java.time.Duration
import java.util.Date

import akka.actor.ActorRef
import com.example.unfilter.models.Tid
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

  case class Register(id: Tid, socket: WebSocket) extends ClientAction

  case class DeRegister(id: Tid, socket: WebSocket) extends ClientAction

  case class RegisterForReport(id: Tid, socket: WebSocket) extends ClientAction

  case class Usage(id: Tid, duration: Duration)

}




