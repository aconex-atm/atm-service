package com.example.unfilter.util

import com.example.unfilter.models.UsageStat
import org.joda.time.format.DateTimeFormat
import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.native.JsonMethods._


trait UsageStatJsonConverter {

  val fmt = DateTimeFormat.forPattern("yyyy-mm-dd")

  def toJson(u: UsageStat): JValue =
    ("date" -> fmt.print(u.date)) ~
      ("key" -> u.duration) ~
      ("count" -> u.count)


  def toJson(us: List[UsageStat]): String = pretty(us.map(toJson(_)))

  def pretty(jValue: JValue): String = compact(render(jValue))
}
