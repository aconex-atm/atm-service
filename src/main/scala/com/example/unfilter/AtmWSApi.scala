package com.example.unfilter

import akka.actor.{ActorRef, ActorSystem, Props}
import com.example.unfilter.repos.NotifierRepository.Register
import com.example.unfilter.repos.{NotifierRepository, ToiletNotifier}
import io.netty.channel.ChannelHandler.Sharable
import org.json4s.JValue
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods.{compact, render}
import unfiltered.netty.ServerErrorResponse
import unfiltered.netty.websockets.{Open, PassHandler, Plan}
import unfiltered.request._


@Sharable
class AtmWSApi(val system : ActorSystem, val notifierRepository: ActorRef) extends Plan with ServerErrorResponse{

  def intent = {
    case Path(Seg("ts" :: id :: "subscribe" :: Nil)) => {
      case Open(socket) => {

        val subscriber = system.actorOf(Props(new ToiletNotifier(id, socket)))
        notifierRepository ! Register(subscriber)

        socket.send(pretty("message" -> "Subscribed"))
      }
    }
  }

  override def pass: PassHandler = _.fireChannelRead(_)

  def pretty(jValue: JValue): String = compact(render(jValue))

}
