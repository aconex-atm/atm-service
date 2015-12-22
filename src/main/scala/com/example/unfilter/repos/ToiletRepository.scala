package com.example.unfilter.repos

import akka.actor.Actor
import com.example.unfilter.Message.{Enquiry, Occupied, Vacant}
import com.example.unfilter.models.{Tid, ToiletSlot}

class ToiletRepository extends Actor {

  var ts = List("G", "1", "2", "3", "4", "5")

  var occupied: List[Tid] = List()

  override def receive: Actor.Receive = {

    case Occupied(id) => {
      occupied = id :: occupied
    }

    case Vacant(id) => {
      occupied = occupied filter (_ != id)
    }

    case Enquiry(id, Some(asker)) => {
      asker ! ToiletSlot(id, occupied.exists(_ == id))
    }

  }
}
