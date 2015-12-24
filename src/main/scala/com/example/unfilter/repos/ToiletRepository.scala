package com.example.unfilter.repos

import java.util.concurrent.TimeUnit

import akka.actor.Actor
import com.example.unfilter.Message._
import com.example.unfilter.models.{Tid, ToiletEvent, ToiletSlot, UsageStat}
import org.joda.time.DateTime

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}


class ToiletRepository extends Actor with ToiletEventStore {

  import scala.concurrent.ExecutionContext.Implicits.global


  var ts = List("G", "1", "2", "3", "4", "5")

  var occupied: List[Tid] = List()

  override def receive: Actor.Receive = {

    case a: ToiletEvent => {
      if (a.occupied) {
        occupied = a.id :: occupied
      }
      enqueue(a)
    }

    case Enquiry(id, _, Some(asker)) => {
      asker ! ToiletSlot(id, occupied.exists(_ == id))
    }

    case Usage(id, duration) => {
      val result: List[UsageStat] = Await.result(usageFor(id, duration), Duration(2, TimeUnit.SECONDS))
      sender() ! result
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