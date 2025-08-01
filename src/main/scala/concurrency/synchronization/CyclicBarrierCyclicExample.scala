package il.co.dotcore.zio.playground.concurrency.synchronization

import zio._
import zio.concurrent.CyclicBarrier

object CyclicBarrierCyclicExample extends ZIOAppDefault {

  def task(name: String) =
    for {
      b <- ZIO.service[CyclicBarrier]
      _ <- ZIO.debug(s"task-$name: started my job right now!")
      d <- Random.nextLongBetween(1000, 10000)
      _ <- ZIO.sleep(Duration.fromMillis(d))
      _ <- ZIO.debug(
        s"task-$name: finished my job and waiting for other parties to finish their jobs"
      )
      _ <- b.await
      _ <- ZIO.debug(
        s"task-$name: the barrier is now broken, so I'm going to exit immediately!"
      )
    } yield ()

  def run =
    for {
      b <- CyclicBarrier.make(
        parties = 3,
        action = ZIO.debug(
          "The barrier is released right now!" +
            "I can do some effectful actions on release of barrier."
        )
      )
      tasks = task("1") <&>
        task("2") <&>
        task("3") <&>
        task("4") <&>
        task("5")
      _ <- tasks.provide(ZLayer.succeed(b))
    } yield ()
}
