package com.example.unfilter

import akka.actor.{ActorRef, ActorSystem}
import com.example.unfilter.Message.{RegisterForReport, DeRegister, Register}
import com.example.unfilter.models.Tid
import io.netty.channel.ChannelHandler.Sharable
import unfiltered.netty.ServerErrorResponse
import unfiltered.netty.websockets.{Close, Open, PassHandler, Plan}
import unfiltered.request._


@Sharable
class WSApi(val system: ActorSystem, val subscriberRepository: ActorRef, val reportSubscriberRepository: ActorRef) extends Plan with ServerErrorResponse {

  def intent = {
    case Path(Seg("level" :: levelId :: "room" :: roomId :: "slot" :: slotId :: "subscribe" :: Nil)) => {
      case Open(socket) => {
        subscriberRepository ! Register(Tid(levelId, roomId, slotId), socket)
      }
      case Close(socket) => {
        subscriberRepository ! DeRegister(Tid(levelId, roomId, slotId), socket)
      }
    }

    case Path(Seg("report" :: "level" :: levelId :: "room" :: roomId :: "slot" :: slotId :: "usage" :: Nil)) => {
      case Open(socket) => {
        reportSubscriberRepository ! RegisterForReport(Tid(levelId, roomId, slotId), socket)
      }
      case Close(socket) => {
        reportSubscriberRepository ! DeRegister(Tid(levelId, roomId, slotId), socket)
      }
    }
  }

  override def pass: PassHandler = _.fireChannelRead(_)
}
