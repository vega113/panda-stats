package com.panda

import java.io.File

import akka.actor.ActorSystem
import akka.pattern.pipe
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import akka.testkit.TestProbe
import org.apache.commons.io.FileUtils
import org.scalatest.FlatSpec

import scala.collection.JavaConversions._
import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * Created by vega on 02/09/2016.
  */
class FileReaderTest extends FlatSpec {

  implicit val system = ActorSystem("test")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  "FileReader" should "read one line" in {
    val tmpFile = File.createTempFile("oneLine", ".tmp")
    val firstLine = "one line"
    Future {
      FileUtils.writeLines(tmpFile, List(firstLine), false)
    }
    val probe = TestProbe()
    FileReader.readContinuously(tmpFile, "utf-8").runWith(Sink.head).pipeTo(probe.ref)
    probe.expectMsg(100.millis, firstLine)
  }

  it should "read three lines" in {
    val tmpFile = File.createTempFile("threeLines", ".tmp")
    val probe = TestProbe()
    val lastLine = "last line"
    Future {
      FileUtils.writeLines(tmpFile, List("one line", "another line"), false)
    }.andThen {
      case _ => FileUtils.writeLines(tmpFile, List(lastLine), true)
    }

    FileReader.readContinuously(tmpFile, "utf-8").grouped(3).map(x => x(2))
      .runWith(Sink.head).pipeTo(probe.ref)
    probe.expectMsg(100.millis, lastLine )
  }
}
