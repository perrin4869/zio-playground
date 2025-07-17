package il.co.dotcore.zio.playground

import zio._
import zio.concurrent._

object ReentrantLockReentrancy extends ZIOAppDefault {

  def task(l: ReentrantLock, i: Int): ZIO[Any, Nothing, Unit] = for {
    fn <- ZIO.fiberId.map(_.threadName)
    _ <- l.lock
    hc <- l.holdCount
    _ <- ZIO.debug(
      s"$fn (re)entered the critical section and now the hold count is $hc"
    )
    _ <- ZIO.when(i > 0)(task(l, i - 1))
    _ <- l.unlock
    hc <- l.holdCount
    _ <- ZIO.debug(
      s"$fn exited the critical section and now the hold count is $hc"
    )
  } yield ()

  def run =
    for {
      l <- ReentrantLock.make()
      _ <- task(l, 2) zipPar task(l, 3)
    } yield ()

}
