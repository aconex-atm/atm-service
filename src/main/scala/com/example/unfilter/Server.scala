package com.example.unfilter

import unfiltered.netty.Http


object Server extends App {
//  unfiltered.jetty.Server.local(8080).plan(PushApi).run()
//  unfiltered.netty.Server.http(8080).plan(PushApi).run()

  Http(8080).chunked(1048576).plan(PushApi).run()
}

