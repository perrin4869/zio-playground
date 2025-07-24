package il.co.dotcore.zio.playground.streaming.zstream.error_handling

import zio._
import zio.stream._

object RetryFailing extends ZIOAppDefault {
  val numbers = ZStream(1, 2, 3) ++
    ZStream
      .fromZIO(
        Console.print("Enter a number: ") *> Console.readLine
          .flatMap(x =>
            x.toIntOption match {
              case Some(value) => ZIO.succeed(value)
              case None        => ZIO.fail("NaN")
            }
          )
      )
      .retry(Schedule.exponential(1.second))

  override def run = for {
    _ <- Console.print("stream: ") *>
      numbers.runForeachChunk(Console.printLine(_)) *> Console.print("\n")
  } yield ()
}
