package com.panda

import akka.actor.Actor
import com.panda.domain._
import scala.collection._

/**
  * Created by vega on 02/09/2016.
  */
class EventsActor extends Actor {
  private val eventsCountMap = mutable.Map[String, Long]()
  private val wordsCountMap = mutable.Map[String, Long]()

  override def receive: Receive = {
    case Event(eventType, data, timestamp) =>
      incrementCount(eventsCountMap, eventType)
      incrementCount(wordsCountMap, data)

    case GetEventCount =>
      sender ! eventsCountMap.toList.map {
        case (eventType, count) => CountData(eventType, count)
      }

    case GetWordCount =>
      sender ! wordsCountMap.toList.map {
        case (data, count) => CountData(data, count)
      }
  }

  private def incrementCount(map: mutable.Map[String, Long], key: String): Unit = {
    val currentCount = map.getOrElse(key, 0L)
    map.update(key, currentCount + 1L)
  }
}