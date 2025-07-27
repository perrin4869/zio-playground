package il.co.dotcore.zio.playground.streaming.zchannel.operations

import zio._
import zio.stream._

object Concatenating extends ZIOAppDefault {
  override def run = for {
    _ <- Console.print("concatOut: ") *> ZChannel
      .writeAll("a", "b", "c")
      .mapOut { l =>
        ZChannel.writeAll((1 to 3).map(i => s"$l$i"): _*)
      }
      .concatOut
      .runCollect
      .debug
    // Output: (Chunk(a1,a2,a3,b1,b2,b3,c1,c2,c3),())
    _ <- Console.print("concatAll: ") *> ZChannel
      .concatAll(
        ZChannel
          .writeAll("a", "b", "c")
          .mapOut { l =>
            ZChannel.writeAll((1 to 3).map(i => s"$l$i"): _*)
          }
      )
      .runCollect
      .debug
    // Output: (Chunk(a1,a2,a3,b1,b2,b3,c1,c2,c3),())
  } yield ()
}
