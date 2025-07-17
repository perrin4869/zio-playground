package il.co.dotcore.zio.playground.concurrency.primitives

import java.util.concurrent.TimeUnit
import zio._
import zio.Console._

object SemaphoreExample extends ZIOAppDefault {
  def task(i: Int) = for {
    _ <- printLine(s"start $i")
    _ <- ZIO.sleep(Duration(2, TimeUnit.SECONDS))
    _ <- printLine(s"end $i")
  } yield ()

  val semTask = (sem: Semaphore, i: Int) =>
    for {
      _ <- sem.withPermit(task(i))
    } yield ()

  val semTaskSeq = (sem: Semaphore) => (1 to 3).map(i => semTask(sem, i))

  val program = for {
    sem <- Semaphore.make(permits = 1)
    // seq <- ZIO.succeed(semTaskSeq(sem))
    seq = semTaskSeq(sem)
    _ <- ZIO.collectAllPar(seq)
  } yield ()

  override def run = program.debug
}
