package il.co.dotcore.zio.playground.concurrency.primitives

import zio._

object QueueExample extends ZIOAppDefault {

  val res1: UIO[Unit] = for {
    queue <- Queue.bounded[Int](100)
    _ <- queue.offer(1)
  } yield ()

  val res2: UIO[Unit] = for {
    queue <- Queue.bounded[Int](1)
    _ <- queue.offer(1)
    f <- queue.offer(1).fork // will be suspended because the queue is full
    _ <- queue.take
    _ <- f.join
  } yield ()

  val res3: UIO[Unit] = for {
    queue <- Queue.bounded[Int](100)
    items = Range.inclusive(1, 10).toList
    _ <- queue.offerAll(items)
  } yield ()

  val oldestItem: UIO[String] = for {
    queue <- Queue.bounded[String](100)
    f <- queue.take.fork // will be suspended because the queue is empty
    _ <- queue.offer("something")
    v <- f.join
  } yield v

  val polled: UIO[Option[(Int, Int, Option[Int])]] = for {
    queue <- Queue.bounded[Int](100)
    _ <- queue.offer(10)
    _ <- queue.offer(20)
    head <- queue.poll.map(_.get)
    head2 <- queue.poll.map(_.get)
    head3 <- queue.poll
  } yield Some((head, head2, head3))

  val taken: UIO[Chunk[Int]] = for {
    queue <- Queue.bounded[Int](100)
    _ <- queue.offer(10)
    _ <- queue.offer(20)
    chunk <- queue.takeUpTo(5)
  } yield chunk

  val all: UIO[Chunk[Int]] = for {
    queue <- Queue.bounded[Int](100)
    _ <- queue.offer(10)
    _ <- queue.offer(20)
    chunk <- queue.takeAll
  } yield chunk

  val takeFromShutdownQueue: UIO[Unit] = for {
    queue <- Queue.bounded[Int](3)
    f <- queue.take.fork
    _ <- queue.shutdown // will interrupt f
    _ <- f.join // Will terminate
  } yield ()

  val awaitShutdown: UIO[Unit] = for {
    queue <- Queue.bounded[Int](3)
    p <- Promise.make[Nothing, Boolean] // NOTE: why is this here?
    f <- queue.awaitShutdown.fork
    _ <- queue.shutdown
    _ <- f.join
  } yield ()

  override def run = res1.debug
  // override def run = res2.debug
  // override def run = res3.debug
  // override def run = oldestItem.debug
  // override def run = polled.debug
  // override def run = taken.debug
  // override def run = all.debug
  // override def run = takeFromShutdownQueue.cause.debug
  // override def run = awaitShutdown.debug
}
