package com.example.unfilter.repos

import java.util.concurrent.TimeUnit

import akka.actor._
import akka.util.Timeout
import com.example.unfilter.Message._
import unfiltered.netty.websockets.WebSocket
import scala.concurrent.duration.FiniteDuration
import scala.util.{Success, Failure}


class NotifierRepository extends Actor {

  implicit val timeout = Timeout(FiniteDuration(1, TimeUnit.SECONDS))

  import context._

  val sockets = Map[WebSocket, ActorRef]()


  override def receive: Receive = subscribedOn(sockets)

  val toiletsSelection: ActorSelection = system.actorSelection(ActorPath.fromString("akka://system/user/toilets"))

  def subscribedOn(sockets: Map[WebSocket, ActorRef]): Receive = {

    case Register(id, socket) => {

      val subscriber = system.actorOf(Props(new ToiletNotifier(id, socket)))

      toiletsSelection.resolveOne().onComplete {
        case Success(toilet) => {
          toilet ! Enquiry(id, Some(subscriber))
        }
        case Failure(e) => {
          println(e.getMessage)
        }
      }

      become(subscribedOn(sockets + (socket -> subscriber)))
    }

    case DeRegister(id, socket) => {
      sockets.get(socket).map(_ ! Quit)
      become(subscribedOn(sockets - socket))
    }

    case action: ToiletAction => sockets.values.map(_ ! action)
  }



}