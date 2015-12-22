package com.example.unfilter


import akka.actor.{ActorRef, ActorSystem}
import akka.util.Timeout
import com.example.unfilter.Message.{Occupied, Vacant}
import com.example.unfilter.models.{Tid, ToiletSlot}
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
class RestApi(val system: ActorSystem, val toilets: ActorRef, val notifiers: ActorRef) extends Plan with ServerErrorResponse {

  implicit val timeout = Timeout(1 seconds)


  def intent = {

    case req@POST(Path(Seg("level" :: levelId :: "room" :: roomId :: "slot" :: slotId :: "occupied" :: Nil))) => {
      toilets ! Occupied(Tid(levelId, roomId, slotId))
      notifiers ! Occupied(Tid(levelId, roomId, slotId))

      req.respond(Ok)
    }

    case req@POST(Path(Seg("level" :: levelId :: "room" :: roomId :: "slot" :: slotId :: "vacant" :: Nil))) => {
      toilets ! Vacant(Tid(levelId, roomId, slotId))
      notifiers ! Vacant(Tid(levelId, roomId, slotId))

      req.respond(Ok)
    }
  }


  def errorResponse(e: Throwable) = InternalServerError ~> ResponseString(error(e))

  def toJson(t: ToiletSlot): String =
    pretty(("occupied" -> t.occupied))

  def error(t: Throwable): String = pretty("error" -> t.getMessage)


  def pretty(jValue: JValue): String = compact(render(jValue))
}
