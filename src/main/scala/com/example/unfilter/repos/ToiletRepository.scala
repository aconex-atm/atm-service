package com.example.unfilter.repos

import akka.actor.Actor
import com.example.unfilter.models.Toilet
import com.example.unfilter.repos.ToiletRepository.{Enquiry, Occupied, Vacant}

class ToiletRepository extends Actor {

  var ts = List("G", "1", "2", "3", "4", "5")

  var occupied: List[String] = List()

  override def receive: Actor.Receive = {
    case Occupied(id) => {
      occupied = id :: occupied
    }

    case Vacant(id) => {
      occupied = occupied filter (_ != id)
    }

    case Enquiry(id) => {
      sender ! Toilet(id, occupied.exists(_ == id))
    }
    case Enquiry => {
      sender ! ts.map(id => Toilet(id, occupied.exists(_ == id)))
    }
  }
}

object ToiletRepository {

  sealed trait ToiletAction

  case class Occupied(id: String) extends ToiletAction

  case class Vacant(id: String) extends ToiletAction

  case class Enquiry(id: String) extends ToiletAction

}

