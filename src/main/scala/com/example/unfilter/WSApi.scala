package com.example.unfilter

import akka.actor.{ActorRef, ActorSystem}
import com.example.unfilter.Message.{DeRegister, Register}
import io.netty.channel.ChannelHandler.Sharable
import unfiltered.netty.ServerErrorResponse
import unfiltered.netty.websockets.{Close, Open, PassHandler, Plan}
import unfiltered.request._


@Sharable
class WSApi(val system: ActorSystem, val notifiers: ActorRef) extends Plan with ServerErrorResponse {

  def intent = {
    case Path(Seg("ts" :: id :: "subscribe" :: Nil)) => {
      case Open(socket) => {
        notifiers ! Register(id, socket)
      }
      case Close(socket) => {
        notifiers ! DeRegister(id, socket)
      }
    }
  }

  override def pass: PassHandler = _.fireChannelRead(_)
}
