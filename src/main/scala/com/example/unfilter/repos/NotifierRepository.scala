package com.example.unfilter.repos

import akka.actor.{Actor, ActorRef}
import com.example.unfilter.repos.NotifierRepository.Register
import com.example.unfilter.repos.ToiletRepository.ToiletAction

class NotifierRepository extends Actor {
  import context._

  val subscribers: List[ActorRef] = List()

  override def receive: Receive = subscribed(subscribers)

  def subscribed(subscribers: List[ActorRef]): Receive = {
    case Register(subscriber) => become(subscribed(subscriber :: subscribers))
    case action: ToiletAction => subscribers.map(_ ! action)
  }
}

object NotifierRepository {

  trait Action

  case class Register(subscriber: ActorRef) extends Action

}
