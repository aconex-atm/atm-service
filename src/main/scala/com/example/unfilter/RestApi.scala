package com.example.unfilter


import akka.actor.{ActorRef, ActorSystem}
import akka.util.Timeout
import com.example.unfilter.models.{Tid, ToiletEvent, ToiletSlot}
import io.netty.channel.ChannelHandler.Sharable
import org.json4s.JValue
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods.{compact, render}
import unfiltered.netty.ServerErrorResponse
import unfiltered.netty.async.Plan
import unfiltered.request._
import unfiltered.response._

import scala.concurrent.duration._


@Sharable
class RestApi(val system: ActorSystem, val toiletRepository: ActorRef) extends Plan with ServerErrorResponse {

  implicit val timeout = Timeout(1 seconds)


  def intent = {

    case req@POST(Path(Seg("level" :: levelId :: "room" :: roomId :: "slot" :: slotId :: "occupied" :: Nil))) => {
      val occupied: ToiletEvent = ToiletEvent.occupied(Tid(levelId, roomId, slotId))
      toiletRepository ! occupied
      req.respond(Ok)
    }

    case req@POST(Path(Seg("level" :: levelId :: "room" :: roomId :: "slot" :: slotId :: "vacant" :: Nil))) => {
      val vacant: ToiletEvent = ToiletEvent.vacant(Tid(levelId, roomId, slotId))
      toiletRepository ! vacant
      req.respond(Ok)
    }
  }


  def errorResponse(e: Throwable) = InternalServerError ~> ResponseString(error(e))

  def toJson(t: ToiletSlot): String =
    pretty(("occupied" -> t.occupied))

  def error(t: Throwable): String = pretty("error" -> t.getMessage)

  def pretty(jValue: JValue): String = compact(render(jValue))
}
