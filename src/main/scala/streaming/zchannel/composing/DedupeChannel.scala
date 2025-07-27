package il.co.dotcore.zio.playground.streaming.zchannel.composing

import zio._
import zio.stream._

import scala.collection.immutable.HashSet

object DedupeChannel extends ZIOAppDefault {
  val dedup =
    ZChannel.fromZIO(Ref.make[HashSet[Int]](HashSet.empty)).flatMap { ref =>
      lazy val inner: ZChannel[Any, Any, Int, Any, Nothing, Int, Unit] =
        ZChannel.readWith(
          (i: Int) =>
            ZChannel
              .fromZIO(ref.modify(s => (s contains i, s incl i)))
              .flatMap {
                case true  => ZChannel.unit
                case false => ZChannel.write(i)
              } *> inner,
          (_: Any) => ZChannel.unit,
          (_: Any) => ZChannel.unit
        )
      inner
    }

  def run =
    (ZChannel.writeAll(1, 2, 2, 3, 3, 4, 2, 5, 5) >>> dedup).runCollect.debug
}
// Output:
// (Chunk(1,2,3,4,5),())
