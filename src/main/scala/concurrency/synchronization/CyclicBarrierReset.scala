package il.co.dotcore.zio.playground.concurrency.synchronization

import zio._
import zio.concurrent.CyclicBarrier

object CyclicBarrierReset extends ZIOAppDefault {
  def task(name: String, b: CyclicBarrier) =
    for {
      _ <- ZIO.debug(s"task-$name: started my job right now!")
      _ <- b.await
      _ <- ZIO.debug(
        s"task-$name: the barrier is now released, " +
          s"so I'm going to exit immediately!"
      )
    } yield ()

  def run =
    for {
      b <- CyclicBarrier.make(3)
      f1 <- task("1", b).fork
      f2 <- task("2", b).fork
      f3 <-
        (ZIO.sleep(1.second) *> task("3", b))
          .onInterrupt(
            ZIO.debug(
              "task-3: I started my job with some delay! " +
                "so before getting the chance to await on the barrier, " +
                "the reset operation interrupted me!"
            )
          )
          .fork
      _ <- f1.status.repeatWhile(!_.isInstanceOf[Fiber.Status.Suspended])
      _ <- f2.status.repeatWhile(!_.isInstanceOf[Fiber.Status.Suspended])
      _ <- b.waiting.debug("waiting fibers before reset")
      _ <- ZIO
        .whenZIO(f3.status.map(_.isInstanceOf[Fiber.Status.Running]))(b.reset)
      _ <- b.waiting.debug("waiting fibers after reset")
      _ <- f1.join
      _ <- f2.join
      _ <- f3.join
    } yield ()
}
