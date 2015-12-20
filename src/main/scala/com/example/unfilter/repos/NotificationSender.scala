package com.example.unfilter.repos

import akka.actor.Actor
import com.example.unfilter.repos.NotificationSender.Push
import com.example.unfilter.models.Notification

import org.json4s.native.JsonMethods.parse
import com.amazonaws.services.sns.model.PublishRequest
import org.json4s.DefaultFormats

object NotificationSender {
  case class Push(json: String)

}
class NotificationSender extends Actor {
  implicit val formats = DefaultFormats

  override def receive: Actor.Receive = {
    case Push(json) => {
      val notification = parse(json).extract[Notification]


      val message: String = """{"default":"This is the default Message",""" +
        """"APNS_SANDBOX":"{ \"aps\" : { \"alert\" : \""""+ notification.message + """\", \"badge\" : 1,\"sound\" :\"default\",\"category\" :\"MESSAGE\"}}",""" +
        """"GCM":"{\"delay_while_idle\":true,\"collapse_key\":\"Welcome\",\"data\":{\"message\":\"""" + notification.message + """\",\"url\":\"http://www.amazon.com/\"},\"time_to_live\":125,\"dry_run\":false}"}"""

      val req = new PublishRequest

      req.setMessage(message)
      req.setSubject(notification.subject)
      req.setTargetArn(notification.target)
      req.setMessageStructure("json")

      sender ! sns.publish(req).getMessageId
    }
  }
}
