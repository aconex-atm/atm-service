package com.example.unfilter.repos

import java.util.concurrent.TimeUnit

import akka.actor.Actor
import akka.util.ByteString
import com.example.unfilter.Env._
import com.example.unfilter.Message._
import com.example.unfilter.models.{RawEvent, Tid, ToiletSlot, UsageStat}
import com.example.unfilter.util.EventJsonConverter
import org.joda.time.DateTime
import org.json4s.native.JsonMethods._
import redis.{ByteStringDeserializer, ByteStringSerializer, RedisClient}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}


class ToiletRepository extends Actor {

  import scala.concurrent.ExecutionContext.Implicits.global

  implicit object OccupiedSerializer extends ByteStringSerializer[Occupied] with EventJsonConverter {
    override def serialize(data: Occupied): ByteString = ByteString(toJson(data))
  }

  implicit object VacantEventSerializer extends ByteStringSerializer[Vacant] with EventJsonConverter {
    override def serialize(data: Vacant): ByteString = ByteString(toJson(data))
  }

  implicit object RawEventDeserializer extends ByteStringDeserializer[RawEvent] with EventJsonConverter {

    override def deserialize(bs: ByteString): RawEvent = {
      val s = bs.decodeString("UTF-8")
      println(s)
      parse(s).extract[RawEvent]
    }
  }


  val KEY_EVENTS: String = "toilet-events"

  var ts = List("G", "1", "2", "3", "4", "5")

  var occupied: List[Tid] = List()

  var redisHost: String = of("REDIS_HOST").getOrElse("127.0.0.1")

  var redisClient = RedisClient(redisHost)

  override def receive: Actor.Receive = {

    case a: Occupied => {
      occupied = a.id :: occupied
      redisClient.lpush(KEY_EVENTS, a)
    }

    case a: Vacant => {
      occupied = occupied filter (_ != a.id)
      redisClient.lpush(KEY_EVENTS, a)
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
    val r: Future[Seq[RawEvent]] = redisClient.lrange[RawEvent](KEY_EVENTS, 0, -1)
    r.map {
      _.asInstanceOf[Seq[RawEvent]].filter(_.occupied).groupBy(_.jodaTime.hourOfDay().get).mapValues(
        es => UsageStat(es.head.jodaTime, durationOf(es.head.jodaTime), es.length)
      ).values.toList
    }
  }


  def durationOf(datetime: DateTime): String = {
    val hourOfDay = datetime.hourOfDay().get()
    s"${hourOfDay}-${hourOfDay + 1}"
  }
}


//def collect(collected: List[ToiletEvent], remaining : List[ToiletEvent]): Seq[ToiletEvent] = remaining match {
//case head :: second :: rest if head.getClass == second.getClass => collect(head :: collected, rest)
//case head :: second :: rest if head.getClass != second.getClass => collect(head :: second:: collected, rest)
//case List() => collected
//}
//
