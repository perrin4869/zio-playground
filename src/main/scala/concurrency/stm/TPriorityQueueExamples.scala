package il.co.dotcore.zio.playground.stm

import zio._
import zio.stm._

object TPriorityQueueExamples extends ZIOAppDefault {

  val minQueue: STM[Nothing, TPriorityQueue[Int]] = TPriorityQueue.empty
  val maxQueue: STM[Nothing, TPriorityQueue[Int]] =
    TPriorityQueue.empty(Ordering[Int].reverse)

  val queue: STM[Nothing, TPriorityQueue[Int]] =
    for {
      queue <- TPriorityQueue.empty[Int]
      _ <- queue.offerAll(List(2, 4, 6, 3, 5, 6))
    } yield queue

  val sorted: STM[Nothing, Chunk[Int]] =
    for {
      queue <- TPriorityQueue.empty[Int]
      _ <- queue.offerAll(List(2, 4, 6, 3, 5, 6))
      sorted <- queue.takeAll
    } yield sorted

  val size: STM[Nothing, Int] =
    for {
      queue <- TPriorityQueue.empty[Int]
      _ <- queue.offerAll(List(2, 4, 6, 3, 5, 6))
      size <- queue.size
    } yield size

  val program = for {
    _ <- queue.commit.flatMap(_.toList.commit).debug("queue")
    _ <- sorted.commit.debug("sorted")
    _ <- size.commit.debug("size")
  } yield ()

  def run = program.debug
}
