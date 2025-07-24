package il.co.dotcore.zio.playground.streaming.zstream.operations

import zio._
import zio.stream._
import zio.Console._

object Scanning extends ZIOAppDefault {
  val scan = ZStream(1, 2, 3, 4, 5).scan(0)(_ + _)
  // Output: 0, 1, 3, 6, 10, 15
  // Iterations:
  //        =>  0 (initial value)
  //  0 + 1 =>  1
  //  1 + 2 =>  3
  //  3 + 3 =>  6
  //  6 + 4 => 10
  // 10 + 5 => 15

  val fold = ZStream(1, 2, 3, 4, 5).runFold(0)(_ + _)
  // Output: 15 (ZIO effect containing 15)

  override def run = for {
    // scan is a special case of mapAccum
    _ <- print("scan: ") *> scan
      .runForeachChunk(printLine(_))
    _ <- print("fold: ") *> fold.flatMap(printLine(_))
  } yield ()
}
