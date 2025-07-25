package il.co.dotcore.zio.playground.streaming.zsink.operations

import zio._
import zio.stream._
import zio.Console._

object Contramap extends ZIOAppDefault {

  val numericSum: ZSink[Any, Nothing, Int, Nothing, Int] =
    ZSink.sum[Int]
  val stringSum: ZSink[Any, Nothing, String, Nothing, Int] =
    numericSum.contramap((x: String) => x.toInt)

  val sum: ZIO[Any, Nothing, Int] =
    ZStream("1", "2", "3", "4", "5").run(stringSum)
  // Output: 15

  override def run = for {
    _ <- sum.debug
  } yield ()
}
