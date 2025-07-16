package il.co.dotcore.myziotest

import zio._

object FiberStatus extends ZIOAppDefault {
  def run =
    (for {
      f1 <- ZIO.never.fork
      f2 <- f1.await.fork
      blockingOn <- f2.status
        .collect(()) { case Fiber.Status.Suspended(_, _, blockingOn) =>
          blockingOn
        }
        .eventually
    } yield (assert(blockingOn == f1.id))).debug
}
