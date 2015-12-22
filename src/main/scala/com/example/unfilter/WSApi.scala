package com.example.unfilter

import akka.actor.{ActorRef, ActorSystem}
import com.example.unfilter.Message.{DeRegister, Register}
import com.example.unfilter.models.Tid
import io.netty.channel.ChannelHandler.Sharable
import unfiltered.netty.ServerErrorResponse
import unfiltered.netty.websockets.{Close, Open, PassHandler, Plan}
import unfiltered.request._


@Sharable
class WSApi(val system: ActorSystem, val notifiers: ActorRef) extends Plan with ServerErrorResponse {

  def intent = {
    case Path(Seg("level" :: levelId :: "room" :: roomId :: "slot" :: slotId :: "subscribe" :: Nil)) => {
      case Open(socket) => {
        notifiers ! Register(Tid(levelId, roomId, slotId), socket)
      }
      case Close(socket) => {
        notifiers ! DeRegister(Tid(levelId, roomId, slotId), socket)
      }
    }
  }

  override def pass: PassHandler = _.fireChannelRead(_)
}
