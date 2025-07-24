package il.co.dotcore.zio.playground.streaming.zstream

import zio._
import zio.Console._
import zio.stream._

object Scheduling extends ZIOAppDefault {

  val stream = ZStream(1, 2, 3, 4, 5).schedule(Schedule.spaced(1.second))

  override def run = for {
    _ <- print("stream: ") *> stream.foreach(printLine(_))
  } yield ()
}
