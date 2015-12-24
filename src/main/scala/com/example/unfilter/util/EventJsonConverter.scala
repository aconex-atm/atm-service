package com.example.unfilter.util

import java.text.SimpleDateFormat

import com.example.unfilter.models.{Tid, ToiletEvent, ToiletSlot}
import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.native.JsonMethods._


trait EventJsonConverter {

  val fmt = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss")

  implicit val formats = new DefaultFormats {
    override def dateFormatter = fmt
  }

  def toJson(t: ToiletEvent): String =
    pretty {
      ("id" -> toJson(t.id)) ~
        ("time" -> fmt.format(t.time)) ~
        ("name" -> t.name)
    }

  def toJson(id: Tid): JValue = {
    ("levelId" -> id.levelId) ~
      ("gender" -> id.gender) ~
      ("slotId" -> id.slotId)
  }

  def toJson(t: ToiletSlot): String =
    pretty {
      ("id" -> t.id.toString) ~
        ("occupied" -> fmt.format(t.occupied))
    }

  def pretty(jValue: JValue): String = compact(render(jValue))
}
