package il.co.dotcore.zio.playground.streaming.zstream

import zio._
import zio.Console._
import zio.stream._

object ConsumingStreams extends ZIOAppDefault {

  val sum: UIO[Int] = ZStream(1, 2, 3).run(ZSink.sum)

  val s1: ZIO[Any, Nothing, Int] = ZStream(1, 2, 3, 4, 5).runFold(0)(_ + _)
  val s2: ZIO[Any, Nothing, Int] =
    ZStream.iterate(1)(_ + 1).runFoldWhile(0)(_ <= 5)(_ + _)

  override def run = for {
    _ <- sum.debug("sum")
    _ <- s1.debug("s1")
    _ <- s2.debug("s2")
    _ <- print("foreach: ") *> ZStream(1, 2, 3).foreach(printLine(_))
  } yield ()
}
