package il.co.dotcore.zio.playground.concurrency.fibers

import zio._
import zio.Console._

object FiberAwait extends ZIOAppDefault {

  override def run =
    for {
      b <- Random.nextBoolean
      fiber <- (if (b) ZIO.succeed(10)
                else ZIO.fail("The boolean was not true")).fork
      exitValue <- fiber.await
      _ <- exitValue match {
        case Exit.Success(value) => printLine(s"Fiber succeeded with $value")
        case Exit.Failure(cause) => printLine(s"Fiber failed")
      }
    } yield ()
}
