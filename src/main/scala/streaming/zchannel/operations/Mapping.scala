package il.co.dotcore.zio.playground.streaming.zchannel.operations

import zio._
import zio.stream._

object Mapping extends ZIOAppDefault {
  val channel =
    ZChannel
      .fromZIO(Console.readLine("Please enter you name: "))
      .mapError(_.toString)

  override def run = for {
    _ <- Console.print("OutDone: ") *> ZChannel
      .writeAll(1, 2, 3)
      .map(_ => 5)
      .runCollect
      .debug
    // (Chunk(1,2,3),5)
    _ <- Console.print("InDone: ") *> (ZChannel.succeed("5") >>>
      ZChannel
        .readWith(
          (i: Int) => ZChannel.write(ZChannel.write(i)),
          (_: Any) => ZChannel.unit,
          (d: Int) => ZChannel.succeed(d * 2)
        )
        .contramap[String](_.toInt)).runCollect.debug
    // Output: (Chunk(),(10))
    _ <- Console.print("OutErr: ") *> channel.runCollect.cause.debug
    // Output: (Chunk(2,4,6),())
    _ <- Console.print("OutElem: ") *> ZChannel
      .writeAll(1, 2, 3)
      .mapOut(_ * 2)
      .runCollect
      .debug
    // Output: (Chunk(2,4,6),())
    _ <- Console.print("InElem: ") *>
      (ZChannel.write("123") >>> ZChannel
        .read[Int]
        .contramapIn[String](_.toInt * 2)).runCollect.debug
    // Output: (Chunk(),(246))

  } yield ()
}
