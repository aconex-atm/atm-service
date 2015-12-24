package com.example.unfilter.models

import java.util.Date

import org.joda.time.{DateTime, DateTimeZone}


case class ToiletEvent(id: Tid, name: String, time: Date) {

  def occupied: Boolean = name == "Occupied"

  def _time = new DateTime(time)

}

object ToiletEvent {

  def occupied(id: Tid, time: Date = localNow) = ToiletEvent(id, "Occupied", time)

  def vacant(id: Tid, time: Date = localNow) = ToiletEvent(id, "Vacant", time)

  private def localNow: Date = DateTime.now(DateTimeZone.forID("+11")).toDate
}


