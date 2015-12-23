package com.example.unfilter.util

import java.text.SimpleDateFormat

import com.example.unfilter.Message.ToiletEvent
import com.example.unfilter.models.RawEvent
import org.joda.time.format.DateTimeFormat
import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.native.JsonMethods._


trait EventJsonConverter {

  implicit val formats = new DefaultFormats {
    override def dateFormatter = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss")
  }

  val fmt = DateTimeFormat.forPattern("yyyy-mm-dd hh:mm:ss")

  def toJson(t: ToiletEvent): String =
    pretty {
      ("id" -> t.id.toString) ~
        ("time" -> fmt.print(t.time)) ~
        ("name" -> t.name)
    }

  def toJson(t: RawEvent): String =
    pretty {
      ("id" -> t.id.toString) ~
        ("time" -> fmt.print(t.jodaTime)) ~
        ("name" -> t.name)
    }

  def pretty(jValue: JValue): String = compact(render(jValue))
}
