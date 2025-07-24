package il.co.dotcore.zio.playground.streaming.zstream.error_handling

import zio._
import zio.stream._

object RefiningErrors extends ZIOAppDefault {
  val stream: ZStream[Any, Throwable, Int] =
    ZStream.fail(new Throwable)

  val res: ZStream[Any, IllegalArgumentException, Int] =
    stream.refineOrDie { case e: IllegalArgumentException => e }

  override def run = for {
    _ <- Console.print("stream: ") *>
      stream.runForeachChunk(Console.printLine(_)) *> Console.print("\n")
  } yield ()
}
