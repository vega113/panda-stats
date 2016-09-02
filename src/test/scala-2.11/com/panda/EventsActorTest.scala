package com.panda

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import akka.util.Timeout
import com.panda.domain.{CountData, Event, GetEventCount, GetWordCount}
import org.scalatest._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
  * Created by vega on 02/09/2016.
  */
class EventsActorTest extends TestKit(ActorSystem("test")) with ImplicitSender
  with FlatSpecLike with Matchers {

  trait TestData {
    val actorRef = TestActorRef(new EventsActor)
    implicit val timeout = Timeout(100, TimeUnit.MILLISECONDS)
  }

  "EvensActor" should
    "count events" in {
    new TestData {
      actorRef ! Event("foo", "sit", 1472835624)
      actorRef ! Event("bar", "dor", 1472835624)
      actorRef ! Event("foo", "sit", 1472835624)

      val futureEvents = (actorRef ? GetEventCount).asInstanceOf[Future[List[CountData]]]
      val eventsResult = Await.result(futureEvents, 100.millis).sortBy(_.count)
      eventsResult should be(List(
        CountData("bar", 1),
        CountData("foo", 2)
      ))

      val futureWords = (actorRef ? GetWordCount).asInstanceOf[Future[List[CountData]]]
      val wordsResult = Await.result(futureWords, 100.millis).sortBy(_.count)
      wordsResult should be(List(
        CountData("dor", 1),
        CountData("sit", 2)
      ))
    }
  }
}
