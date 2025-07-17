package il.co.dotcore.zio.playground.stm

import zio._
import zio.stm._

object TQueueExamples extends ZIOAppDefault {

  val tQueueBounded: STM[Nothing, TQueue[Int]] = TQueue.bounded[Int](5)
  val tQueueUnbounded: STM[Nothing, TQueue[Int]] = TQueue.unbounded[Int]

  val tQueueOffer: UIO[TQueue[Int]] = (for {
    tQueue <- TQueue.bounded[Int](3)
    _ <- tQueue.offer(1)
  } yield tQueue).commit

  val tQueueOfferAll: UIO[TQueue[Int]] = (for {
    tQueue <- TQueue.bounded[Int](3)
    _ <- tQueue.offerAll(List(1, 2))
  } yield tQueue).commit

  val tQueueTake: UIO[Int] = (for {
    tQueue <- TQueue.bounded[Int](3)
    _ <- tQueue.offerAll(List(1, 2))
    res <- tQueue.take
  } yield res).commit

  val tQueuePoll: UIO[Option[Int]] = (for {
    tQueue <- TQueue.bounded[Int](3)
    res <- tQueue.poll
  } yield res).commit

  val tQueueTakeUpTo: UIO[Chunk[Int]] = (for {
    tQueue <- TQueue.bounded[Int](4)
    _ <- tQueue.offerAll(List(1, 2))
    res <- tQueue.takeUpTo(3)
  } yield res).commit

  val tQueueTakeAll: UIO[Chunk[Int]] = (for {
    tQueue <- TQueue.bounded[Int](4)
    _ <- tQueue.offerAll(List(1, 2))
    res <- tQueue.takeAll
  } yield res).commit

  val tQueueSize: UIO[Int] = (for {
    tQueue <- TQueue.bounded[Int](3)
    _ <- tQueue.offerAll(List(1, 2))
    size <- tQueue.size
  } yield size).commit

  val program = for {
    _ <- tQueueOffer.flatMap(_.peekAll.commit).debug("tQueueOffer")
    _ <- tQueueOfferAll.flatMap(_.peekAll.commit).debug("tQueueOfferAll")
    _ <- tQueueTake.debug("tQueueTake")
    _ <- tQueuePoll.debug("tQueuePoll")
    _ <- tQueueTakeUpTo.debug("tQueueTakeUpTo")
    _ <- tQueueTakeAll.debug("tQueueTakeAll")
    _ <- tQueueSize.debug("tQueueSize")
  } yield ()

  def run = program.debug
}
