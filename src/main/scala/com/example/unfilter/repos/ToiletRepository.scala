package com.example.unfilter.repos

import java.util.concurrent.TimeUnit

import akka.actor.ActorPath._
import akka.actor.{Actor, ActorSelection}
import akka.util.Timeout
import com.example.unfilter.Message._
import com.example.unfilter.models.{Tid, ToiletEvent, ToiletSlot, UsageStat}
import org.joda.time.DateTime

import scala.concurrent.duration.{FiniteDuration, Duration}
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}


class ToiletRepository extends Actor with ToiletEventStore {

  implicit val timeout = Timeout(FiniteDuration(1, TimeUnit.SECONDS))

  import context._

  val subscriberRepositoryPath: String = "akka://system/user/status-subscribers"

  val subscriberSelection: ActorSelection = system.actorSelection(fromString(subscriberRepositoryPath))

  override def receive: Actor.Receive = to(List())

  def to(occupied: List[Tid]): Actor.Receive = {
    case event: ToiletEvent => {
      become(to(event.modified(occupied)))
      enqueue(event)
      notifySubscribers(event.id, occupied.exists(_ == event.id))
    }

    case Enquiry(id, _, Some(asker)) => {
      asker ! ToiletSlot(id, occupied.exists(_ == id))
    }

    case Usage(id, duration) => {
      val result: List[UsageStat] = Await.result(usageFor(id, duration), Duration(2, TimeUnit.SECONDS))
      sender ! result
    }
  }

  def notifySubscribers(id: Tid, occupied: Boolean): Unit = {
    subscriberSelection.resolveOne().onComplete {
      case Success(subscribers) => {
        subscribers ! Updated(ToiletSlot(id, occupied))
      }
      case Failure(e) => {
        println(e.getMessage)
      }
    }
  }

  def usageFor(id: Tid, duration: java.time.Duration): Future[List[UsageStat]] = {
    all.map {
      _.asInstanceOf[Seq[ToiletEvent]].filter(_.occupied).groupBy(_._time.hourOfDay().get).mapValues(
        es => UsageStat(es.head._time, durationOf(es.head._time), es.length)
      ).values.toList.sortBy(_.date.toDate)
    }
  }

  def durationOf(datetime: DateTime): String = {
    val hourOfDay = datetime.hourOfDay().get()
    s"${hourOfDay}-${hourOfDay + 1}"
  }
}