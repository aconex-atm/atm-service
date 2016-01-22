package com.example.unfilter

import akka.actor.{ActorRef, ActorSystem}
import com.example.unfilter.Message.{RegisterForReport, Unsubscribe, Subscribe}
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
        subscriberRepository ! Subscribe(Tid(levelId, roomId, slotId), socket)
      }
      case Close(socket) => {
        subscriberRepository ! Unsubscribe(Tid(levelId, roomId, slotId), socket)
      }
    }

    case Path(Seg("report" :: "level" :: levelId :: "room" :: roomId :: "slot" :: slotId :: "usage" :: Nil)) => {
      case Open(socket) => {
        reportSubscriberRepository ! RegisterForReport(Tid(levelId, roomId, slotId), socket)
      }
      case Close(socket) => {
        reportSubscriberRepository ! Unsubscribe(Tid(levelId, roomId, slotId), socket)
      }
    }
  }

  override def pass: PassHandler = _.fireChannelRead(_)
}
