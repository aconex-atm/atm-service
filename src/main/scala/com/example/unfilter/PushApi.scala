package com.example.unfilter



import scala.util.{Failure, Success}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

import akka.util.Timeout
import akka.pattern.ask
import akka.actor.{Props, ActorSystem}

import unfiltered.request._
import unfiltered.response._
import unfiltered.netty.async.Plan
import unfiltered.netty.ServerErrorResponse

import org.json4s.JsonAST.{JArray, JString}
import org.json4s.native.JsonMethods.{render, compact}
import org.json4s.JValue

import com.example.unfilter.repos.NotificationSender.Push
import com.example.unfilter.repos.{NotificationSender, DeviceRepository}
import com.example.unfilter.repos.DeviceRepository._


object PushApi extends Plan with ServerErrorResponse {

  implicit val system = ActorSystem("system")
  implicit val timeout = Timeout(10 seconds)
  val register = system.actorOf(Props[DeviceRepository])
  val sender = system.actorOf(Props[NotificationSender])

  def intent = {
    case req @ POST(Path(Seg("devices" :: Nil))) => {
      (register ? Create(Body.string(req))).
        mapTo[String].
        onComplete {
          case Success(x) => req.respond(Ok ~> ResponseString(x))
          case Failure(e) => req.respond(errorResponse(e))
      }
    }

    case req @ GET(Path(Seg("devices" :: Nil))) => {
      (register ? All).
        mapTo[List[String]].
        map(toDeviceId).
        onComplete {
          case Success(x) => req.respond(Created ~> ResponseString(x))
          case Failure(e) => req.respond(errorResponse(e))
      }
    }

    case req @ POST(Path(Seg("notifications" :: Nil))) => {
      (sender? Push(Body.string(req))).
        mapTo[String].
        onComplete {
          case Success(x) => req.respond(Created ~> ResponseString(x))
          case Failure(e) => req.respond(errorResponse(e))
      }
    }
  }

  def errorResponse(e: Throwable) = InternalServerError ~> ResponseString(e.getMessage)

  def toDeviceId: (List[String]) => String = {
    arns => pretty(JArray(arns.map(JString(_))))
  }

  def pretty(jValue: JValue) : String = compact(render(jValue))
}
