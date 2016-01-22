package com.example.unfilter.repos

import java.util.concurrent.TimeUnit

import akka.actor.ActorPath.fromString
import akka.actor._
import akka.util.Timeout
import com.example.unfilter.Message._
import com.example.unfilter.actors.ToiletStatusSubscriber
import com.example.unfilter.models.{ToiletSlot, Tid, ToiletEvent}
import unfiltered.netty.websockets.WebSocket

import scala.concurrent.duration.FiniteDuration
import scala.util.{Failure, Success}


class StatusSubscriberRepository extends Actor {

  implicit val timeout = Timeout(FiniteDuration(1, TimeUnit.SECONDS))

  import context._

  val toiletRepositoryPath: String = "akka://system/user/toilets"

  val subscribers = Map[WebSocket, ActorRef]()

  override def receive: Receive = subscribedOn(subscribers)

  val toiletsSelection: ActorSelection = system.actorSelection(fromString(toiletRepositoryPath))

  def subscribedOn(subscribers: Map[WebSocket, ActorRef]): Receive = {

    case Subscribe(id, socket) => {

      val subscriber = system.actorOf(Props(new ToiletStatusSubscriber(id, socket)))

      sendStatusTo(id, subscriber)

      become(subscribedOn(subscribers + (socket -> subscriber)))
    }

    case Unsubscribe(id, socket) => {
      subscribers.get(socket).map(_ ! Quit)
      become(subscribedOn(subscribers - socket))
    }

    case Updated(slot: ToiletSlot) => {
      subscribers.values.map(_ ! slot)
    }
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