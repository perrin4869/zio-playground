package il.co.dotcore.zio.playground.concurrency.synchronization

import zio._
import zio.concurrent._

object AdvancedLatch extends ZIOAppDefault {

  def consume(queue: Queue[Int]): UIO[Nothing] =
    queue.take
      .flatMap(i => ZIO.debug(s"consumed: $i"))
      .forever

  def produce(queue: Queue[Int], latch: CountdownLatch): UIO[Nothing] =
    (Random
      .nextIntBounded(100)
      .tap(i => queue.offer(i))
      .tap(i => ZIO.when(i == 50)(latch.countDown)) *> ZIO.sleep(
      500.millis
    )).forever

  def run =
    for {
      latch <- CountdownLatch.make(5)
      queue <- Queue.unbounded[Int]
      p = ZIO.collectAllParDiscard(ZIO.replicate(10)(produce(queue, latch)))
      c = latch.await *> consume(queue)
      _ <- p <&> c
    } yield ()
}
