package com.example.unfilter

import unfiltered.netty.Http


object Server extends App {

  val host: String = env("HOST").getOrElse("127.0.0.1")

  Http(8080, host).chunked(1048576).plan(ATMApi).run()

  def env(key: String): Option[String] = {
    val value = System.getenv(key)
    if (value == null || value.trim.length == 0) {
      None
    } else {
      Some(value)
    }
  }
}

