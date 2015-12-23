package com.example.unfilter.models

import java.util.Date

import org.joda.time.DateTime


case class RawEvent(id: String, time: Date, name: String) {

  def jodaTime = new DateTime(time)

  def occupied = name == "Occupied"

}
