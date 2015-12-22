package com.example.unfilter

import akka.actor.{ActorRef, ActorSystem}
import com.example.unfilter.Message.{DeRegister, Register}
import io.netty.channel.ChannelHandler.Sharable
import org.json4s.JValue
import org.json4s.native.JsonMethods.{compact, render}
import unfiltered.netty.ServerErrorResponse
import unfiltered.netty.websockets.{Close, Open, PassHandler, Plan}
import unfiltered.request._


@Sharable
class WSApi(val system: ActorSystem, val notifierRepository: ActorRef) extends Plan with ServerErrorResponse {

  def intent = {
    case Path(Seg("ts" :: id :: "subscribe" :: Nil)) => {
      case Open(socket) => {

        notifierRepository ! Register(id, socket)
      }

      case Close(socket) => {
        notifierRepository ! DeRegister(id, socket)
      }
    }
  }

  override def pass: PassHandler = _.fireChannelRead(_)

  def pretty(jValue: JValue): String = compact(render(jValue))

}
