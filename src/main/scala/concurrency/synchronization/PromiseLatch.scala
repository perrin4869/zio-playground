package il.co.dotcore.zio.playground.concurrency.synchronization

import zio._

object PromiseLatch extends ZIOAppDefault {
  def consume(queue: Queue[Int]): UIO[Nothing] =
    queue.take
      .flatMap(i => ZIO.debug(s"consumed: $i"))
      .forever

  def produce(queue: Queue[Int], latch: Promise[Nothing, Unit]): UIO[Nothing] =
    (Random
      .nextIntBounded(100)
      .tap(i => queue.offer(i))
      .tap(i => ZIO.when(i == 50)(latch.succeed(()))) *> ZIO.sleep(
      500.millis
    )).forever

  def run =
    for {
      latch <- Promise.make[Nothing, Unit]
      queue <- Queue.unbounded[Int]
      _ <- produce(queue, latch) <&> (latch.await *> consume(queue))
    } yield ()
}
