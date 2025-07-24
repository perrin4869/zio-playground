package il.co.dotcore.zio.playground.streaming.zpipeline

import zio._
import zio.Console._
import zio.stream._

object Identity extends ZIOAppDefault {

  val stream = ZStream(1, 2, 3).via(ZPipeline.identity[Int])
  // Ouput: 1, 2, 3

  override def run = for {
    _ <- print("stream: ") *> stream.foreach(printLine(_))
  } yield ()
}
