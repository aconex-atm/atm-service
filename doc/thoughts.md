Thoughts on Actor:

- actor is good for asynchronous operation, don't need to care about return value

- actor is good for scalable work

- you need to define a segregation line between synchronize code and actor(asynchronize) code.

- if you care about the return value of a actor, you may want to imply `ask pattern`

- `Future` could also be used to represent a time-consuming operation, and use `mapTo` method to collect the result of `Future`

- json serialization/deserialization, use `spray-json`, have to import a `XXXJsonProtocol` to get implicit Marshaller/Unmarshaller while defining routes

