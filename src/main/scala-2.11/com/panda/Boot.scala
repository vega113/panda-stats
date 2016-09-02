package com.panda

import java.nio.file.Paths
import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import akka.util.Timeout
import com.panda.domain.JsonSupport._
import com.panda.domain._
import spray.json._

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

/**
  * Created by vega on 02/09/2016.
  */
object Boot extends App {

  implicit val system = ActorSystem("boot")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val eventsActor = system.actorOf(Props[EventsActor])

  implicit val timeout = Timeout(100, TimeUnit.MILLISECONDS)

  val file = Paths.get("lines.txt").toFile
  FileReader.readContinuously(file, "utf-8")
    .async
    .map(s => Try(s.parseJson.convertTo[Event]))
    .filter(_.isSuccess)
    .map(_.get)
    .runWith(Sink.actorRef(eventsActor, ()))

  val route =
    path("stats") {
      get {
        complete {
          val eventsCountFuture = (eventsActor ? GetEventCount).asInstanceOf[Future[List[CountData]]]
          val wordsCountFuture = (eventsActor ? GetWordCount).asInstanceOf[Future[List[CountData]]]
          for {
            eventsCount <- eventsCountFuture
            wordsCount <- wordsCountFuture
          } yield HttpEntity(ContentTypes.`text/html(UTF-8)`,
            s"""
               |<h1>Stats</h1>
               | <h4>Events</h4>
               | ${makeHtmlString(eventsCount)}
               | <h4>Words</h4>
               | ${makeHtmlString(wordsCount)}
            """.stripMargin)
        }
      }
    }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
  bindingFuture.onComplete {
    case Success(s) =>
      println(s"Server online, stats are available at http://localhost:8080/stats")
    case Failure(t) =>
      System.err.println("Failed to run the server: " + t.getMessage)
      system.terminate().onComplete {
        case _ => System.exit(1)
      }
  }

  private def makeHtmlString(countDataList: List[CountData]) = {
    countDataList.map(data => s"${data.data}:${data.count}").mkString(", ")
  }
}