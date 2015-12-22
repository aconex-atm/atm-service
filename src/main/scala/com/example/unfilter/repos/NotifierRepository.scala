package com.example.unfilter.repos

import akka.actor.{Actor, ActorRef, Props}
import com.example.unfilter.Message.{DeRegister, Quit, Register, ToiletAction}
import unfiltered.netty.websockets.WebSocket


class NotifierRepository extends Actor {

  import context._

  val sockets = Map[WebSocket, ActorRef]()


  override def receive: Receive = subscribedOn(sockets)

  def subscribedOn(sockets: Map[WebSocket, ActorRef]): Receive = {

    case Register(id, socket) => {
      val subscriber = system.actorOf(Props(new ToiletNotifier(id, socket)))
      become(subscribedOn(sockets + (socket -> subscriber)))
    }

    case DeRegister(id, socket) => {
      sockets.get(socket).map(_ ! Quit)
      become(subscribedOn(sockets - socket))
    }

    case action: ToiletAction => sockets.values.map(_ ! action)
  }
}