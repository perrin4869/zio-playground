package il.co.dotcore.zio.playground.streaming.zstream.error_handling

import zio._
import zio.stream._

object TimingOut extends ZIOAppDefault {
  val stream =
    ZStream(1).schedule(Schedule.delayed(Schedule.duration(20.seconds)))
  case class TimeoutException()
  val timedOut = stream.timeoutFail(new TimeoutException)(10.seconds)

  // val alternative = ZStream.fromZIO(ZIO.attempt(???))
  val alternative = ZStream.fromZIO(ZIO.attempt(42))
  val timedOutToAlternative = stream.timeoutTo(10.seconds)(alternative)

  override def run = for {
    _ <- timedOut.runForeachChunk(Console.printLine(_)).cause.debug("timedOut")
    _ <- timedOutToAlternative
      .runForeachChunk(Console.printLine(_))
      .cause
      .debug("timedOutToAlternative")
  } yield ()
}
