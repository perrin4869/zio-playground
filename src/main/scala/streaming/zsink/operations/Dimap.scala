package il.co.dotcore.zio.playground.streaming.zsink.operations

import zio._
import zio.stream._
import zio.Console._

object Dimap extends ZIOAppDefault {

  val numericSum: ZSink[Any, Nothing, Int, Nothing, Int] =
    ZSink.sum[Int]

  // Convert its input to integers, do the computation and then convert them back to a string
  val sumSink: ZSink[Any, Nothing, String, Nothing, String] =
    numericSum.dimap[String, String](_.toInt, _.toString)

  val sum: ZIO[Any, Nothing, String] =
    ZStream("1", "2", "3", "4", "5").run(sumSink)
  // Output: 15

  override def run = for {
    _ <- sum.debug
  } yield ()
}
