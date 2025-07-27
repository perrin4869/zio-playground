package il.co.dotcore.zio.playground.streaming.zchannel.operations

import zio._
import zio.stream._

object Zipping extends ZIOAppDefault {
  val first = ZChannel.write(1, 2, 3) *> ZChannel.succeed("Done!")
  val second = ZChannel.write(4, 5, 6) *> ZChannel.succeed("Bye!")

  override def run = for {
    _ <- Console.print("zip: ") *> (first <*> second).runCollect.debug
    // Output: (Chunk((1,2,3),(4,5,6)),(Done!,Bye!))
    _ <- Console.print("zipRight: ") *> (first *> second).runCollect.debug
    _ <- Console.print("zipLeft: ") *> (first <* second).runCollect.debug
  } yield ()
}
