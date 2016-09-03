package com.panda

/**
  * Created by vega on 02/09/2016.
  */
import java.io.File
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{SourceQueueWithComplete, Source}
import org.apache.commons.io.input.{Tailer, TailerListenerAdapter}

object FileReader {

  def readContinuously[T](file: File, encoding: String): Source[String, SourceQueueWithComplete[String]] =
    Source.queue[String](bufferSize = 100000, OverflowStrategy.fail).
      mapMaterializedValue { queue =>
        Tailer.create(file, new TailerListenerAdapter {
          override def handle(line: String): Unit = {
            queue.offer(line)
          }
        })
        queue
      }
}
