package com.example.unfilter.repos

import akka.actor.Actor
import akka.util.ByteString
import com.example.unfilter.Env
import com.example.unfilter.models.ToiletEvent
import com.example.unfilter.util.EventJsonConverter
import org.json4s.native.JsonMethods._
import redis.{ByteStringDeserializer, ByteStringSerializer, RedisClient}

import scala.concurrent.Future

trait ToiletEventStore {

  this: Actor =>

  import scala.concurrent.ExecutionContext.Implicits.global

  val KEY_EVENTS: String = "toilet-events"

  val UTF_8: String = "UTF-8"

  val redisHost: String = Env.of("REDIS_HOST").getOrElse("127.0.0.1")

  val redisClient = RedisClient(redisHost)

  implicit object ToiletEventSerializer extends ByteStringSerializer[ToiletEvent] with EventJsonConverter {
    override def serialize(data: ToiletEvent): ByteString = ByteString(toJson(data))
  }

  implicit object ToiletEventDeserializer extends ByteStringDeserializer[ToiletEvent] with EventJsonConverter {
    override def deserialize(bs: ByteString): ToiletEvent = parse(bs.utf8String).extract[ToiletEvent]
  }

  def enqueue(e: ToiletEvent): Unit = {
    redisClient.lpush(KEY_EVENTS, e)
  }

  def all(): Future[List[ToiletEvent]] = {
    redisClient.lrange[ToiletEvent](KEY_EVENTS, 0, -1).map(_.toList)
  }
}
