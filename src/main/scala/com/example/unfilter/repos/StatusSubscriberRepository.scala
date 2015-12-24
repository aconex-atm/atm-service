package com.example.unfilter.repos

import java.util.concurrent.TimeUnit

import akka.actor._
import akka.util.Timeout
import com.example.unfilter.Message._
import com.example.unfilter.actors.ToiletStatusSubscriber
import com.example.unfilter.models.{Tid, ToiletEvent}
import unfiltered.netty.websockets.WebSocket

import scala.concurrent.duration.FiniteDuration
import scala.util.{Failure, Success}


class StatusSubscriberRepository extends Actor {

  implicit val timeout = Timeout(FiniteDuration(1, TimeUnit.SECONDS))

  import context._

  val toiletRepositoryPath: String = "akka://system/user/toilets"

  val subscribers = Map[WebSocket, ActorRef]()

  override def receive: Receive = subscribedOn(subscribers)

  val toiletsSelection: ActorSelection = system.actorSelection(ActorPath.fromString(toiletRepositoryPath))

  def subscribedOn(sockets: Map[WebSocket, ActorRef]): Receive = {

    case Register(id, socket) => {

      val subscriber = system.actorOf(Props(new ToiletStatusSubscriber(id, socket)))

      sendStatusTo(id, subscriber)

      become(subscribedOn(sockets + (socket -> subscriber)))
    }

    case DeRegister(id, socket) => {
      sockets.get(socket).map(_ ! Quit)
      become(subscribedOn(sockets - socket))
    }

    case event: ToiletEvent => sockets.values.map(_ ! event)
  }


  def sendStatusTo(id: Tid, subscriber: ActorRef): Unit = {
    toiletsSelection.resolveOne().onComplete {
      case Success(toiletRepository) => {
        toiletRepository ! Enquiry(id, asker = Some(subscriber))
      }
      case Failure(e) => {
        println(e.getMessage)
      }
    }
  }
}