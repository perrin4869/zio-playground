package il.co.dotcore.zio.playground.concurrency.fibers

import zio._
import zio.Console._

object FiberJoinInterrupted extends ZIOAppDefault {
  def run =
    (
      for {
        fiber <- printLine("Running a job").delay(1.seconds).forever.fork
        _ <- fiber.interrupt.delay(3.seconds)
        _ <- fiber.join // Joining an interrupted fiber
      } yield ()
    ).ensuring(
      printLine(
        "This finalizer will be executed without occurring any deadlock"
      ).orDie
    )
}
