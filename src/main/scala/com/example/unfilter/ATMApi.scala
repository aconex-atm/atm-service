package com.example.unfilter


import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.example.unfilter.models.Toilet
import com.example.unfilter.repos.ToiletRepository
import com.example.unfilter.repos.ToiletRepository._
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
object ATMApi extends Plan with ServerErrorResponse {

  implicit val system = ActorSystem("system")
  implicit val timeout = Timeout(1 seconds)
  val register = system.actorOf(Props[ToiletRepository])

  def intent = {

    case req@POST(Path(Seg("ts" :: id :: "occupied" :: Nil))) => {
      register ! Occupied(id)
      req.respond(Ok)
    }
    case req@POST(Path(Seg("ts" :: id :: "vacant" :: Nil))) => {
      register ! Vacant(id)
      req.respond(Ok)
    }


    case req@GET(Path(Seg("ts" :: id :: Nil))) => {
      (register ? Enquiry(id)).mapTo[Toilet].onComplete {
        case Success(t) => req.respond(Ok ~> ResponseString(toJson(t)))
        case Failure(e) => req.respond(errorResponse(e))
      }
    }

    case req@GET(Path(Seg("ts" :: Nil))) => {
      (register ? Enquiry).mapTo[List[Toilet]].onComplete {
        case Success(ts) => req.respond(Ok ~> ResponseString(toJson(ts)))
        case Failure(e) => req.respond(errorResponse(e))
      }
    }
  }


  def errorResponse(e: Throwable) = InternalServerError ~> ResponseString(error(e))

  def toJson(t: Toilet): String =
    pretty(("id" -> t.id) ~ ("occupied" -> t.occupied))

  def toJson(ts: List[Toilet]): String = pretty( ts.map (
    t => { ("id" -> t.id) ~ ("occupied" -> t.occupied)}
  ))

  def error(t: Throwable): String = pretty ("error" -> t.getMessage)



  def pretty(jValue: JValue): String = compact(render(jValue))
}
