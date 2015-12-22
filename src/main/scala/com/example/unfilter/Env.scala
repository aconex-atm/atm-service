package com.example.unfilter

object Env {

  def of(key: String): Option[String] = {
    val value = System.getenv(key)
    if (value == null || value.trim.length == 0) {
      None
    } else {
      Some(value)
    }
  }

}