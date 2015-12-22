package com.example.unfilter


import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import com.example.unfilter.Message.{Enquiry, Occupied, Vacant}
import com.example.unfilter.models.Toilet
import io.netty.channel.ChannelHandler.Sharable
import org.json4s.JValue
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods.{compact, render}
import unfiltered.netty.ServerErrorResponse
import unfiltered.netty.async.Plan
import unfiltered.request._
import unfiltered.response._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Failure, Success}


@Sharable
class RestApi(val system: ActorSystem, val toilets: ActorRef, val notifiers: ActorRef) extends Plan with ServerErrorResponse {

  implicit val timeout = Timeout(1 seconds)


  def intent = {

    case req@POST(Path(Seg("ts" :: id :: "occupied" :: Nil))) => {
      toilets ! Occupied(id)
      notifiers ! Occupied(id)
      req.respond(Ok)
    }
    case req@POST(Path(Seg("ts" :: id :: "vacant" :: Nil))) => {
      toilets ! Vacant(id)
      notifiers ! Vacant(id)
      req.respond(Ok)
    }


    case req@GET(Path(Seg("ts" :: id :: Nil))) => {
      (toilets ? Enquiry(id)).mapTo[Toilet].onComplete {
        case Success(t) => req.respond(Ok ~> ResponseString(toJson(t)))
        case Failure(e) => req.respond(errorResponse(e))
      }
    }

    case req@GET(Path(Seg("ts" :: Nil))) => {
      (toilets ? Enquiry).mapTo[List[Toilet]].onComplete {
        case Success(ts) => req.respond(Ok ~> ResponseString(toJson(ts)))
        case Failure(e) => req.respond(errorResponse(e))
      }
    }
  }


  def errorResponse(e: Throwable) = InternalServerError ~> ResponseString(error(e))

  def toJson(t: Toilet): String =
    pretty(("id" -> t.id) ~ ("occupied" -> t.occupied))

  def toJson(ts: List[Toilet]): String = pretty(ts.map(
    t => {
      ("id" -> t.id) ~ ("occupied" -> t.occupied)
    }
  ))

  def error(t: Throwable): String = pretty("error" -> t.getMessage)


  def pretty(jValue: JValue): String = compact(render(jValue))
}
