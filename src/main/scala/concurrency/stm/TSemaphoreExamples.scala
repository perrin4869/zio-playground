package il.co.dotcore.zio.playground.stm

import zio._
import zio.stm._

object TSemaphoreExamples extends ZIOAppDefault {

  val tSemaphoreCreate: STM[Nothing, TSemaphore] = TSemaphore.make(10L)

  val tSemaphoreAcq: STM[Nothing, TSemaphore] = for {
    tSem <- TSemaphore.make(2L)
    _ <- tSem.acquire
  } yield tSem

  val tSemaphoreRelease: STM[Nothing, TSemaphore] = for {
    tSem <- TSemaphore.make(1L)
    _ <- tSem.acquire
    _ <- tSem.release
  } yield tSem

  val tSemaphoreAvailable: STM[Nothing, Long] = for {
    tSem <- TSemaphore.make(2L)
    _ <- tSem.acquire
    cap <- tSem.available
  } yield cap

  def yourSTMAction: STM[Nothing, Unit] = STM.unit

  val tSemaphoreWithoutPermit: STM[Nothing, Unit] =
    for {
      sem <- TSemaphore.make(1L)
      _ <- sem.acquire
      a <- yourSTMAction
      _ <- sem.release
    } yield a

  val tSemaphoreWithPermit: IO[Nothing, Unit] =
    for {
      sem <- TSemaphore.make(1L).commit
      a <- sem.withPermit(yourSTMAction.commit)
    } yield a

  val tSemaphoreAcquireNReleaseN: STM[Nothing, Boolean] = for {
    sem <- TSemaphore.make(3L)
    _ <- sem.acquireN(3L)
    cap <- sem.available
    _ <- sem.releaseN(3L)
  } yield cap == 0

  val program = for {
    _ <- tSemaphoreAcq.commit
      .flatMap(_.permits.get.commit)
      .debug("tSemaphoreAcq")
    _ <- tSemaphoreRelease.commit
      .flatMap(_.permits.get.commit)
      .debug("tSemaphoreRelease")
    _ <- tSemaphoreAvailable.commit.debug("tSemaphoreAvailable")
    _ <- tSemaphoreWithoutPermit.commit.debug("tSemaphoreWithoutPermit")
    _ <- tSemaphoreWithPermit.debug("tSemaphoreWithPermit")
    _ <- tSemaphoreAcquireNReleaseN.commit.debug("tSemaphoreAcquireNReleaseN")
  } yield ()

  def run = program.debug
}
