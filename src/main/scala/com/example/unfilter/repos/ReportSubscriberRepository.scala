package com.example.unfilter.repos


import java.time.Duration
import java.util.concurrent.TimeUnit

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import com.example.unfilter.Message.{RegisterForReport, ToiletEvent, Usage}
import com.example.unfilter.actors.ToiletReportSubscriber
import com.example.unfilter.models.{Tid, UsageStat}
import unfiltered.netty.websockets.WebSocket

import scala.concurrent.duration.FiniteDuration
import scala.util.{Failure, Success}

class ReportSubscriberRepository extends Actor {

  implicit val timeout = Timeout(FiniteDuration(1, TimeUnit.SECONDS))

  import context._

  val toiletRepositoryPath: String = "akka://system/user/toilets"

  val subscribers = Map[WebSocket, ActorRef]()

  override def receive: Receive = subscribedOn(subscribers)

  val toiletsSelection: ActorSelection = system.actorSelection(ActorPath.fromString(toiletRepositoryPath))

  def subscribedOn(subscribers: Map[WebSocket, ActorRef]): Receive = {

    case RegisterForReport(id, socket) => {

      val subscriber = system.actorOf(Props(new ToiletReportSubscriber(id, socket)))

      sendReportTo(id, subscriber)

      become(subscribedOn(subscribers + (socket -> subscriber)))
    }

    case event: ToiletEvent => subscribers.values.map(sendReportTo(event.id, _))

  }

  def sendReportTo(id: Tid, subscriber: ActorRef): Unit = {
    toiletsSelection.resolveOne().onComplete {
      case Success(toiletRepository) => {
        (toiletRepository ? Usage(id, Duration.ofHours(12)))
          .mapTo[List[UsageStat]]
          .map {
            subscriber ! _
          }
      }
      case Failure(e) => {
        println(e.getMessage)
      }
    }
  }

}