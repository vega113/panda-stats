package com.panda

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

/**
  * Created by vega on 02/09/2016.
  */
object domain {
  case class UpdateEventTypeCount(eventType: String)

  case object GetEventCount

  case object GetWordCount

  case class CountData(data: String, count: Long)

  case class Event(event_type: String, data: String, timestamp: Long)

  object JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
    implicit val eventFormat = jsonFormat3(Event)
  }
}
