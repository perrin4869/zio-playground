package il.co.dotcore.zio.playground

import zio._

object Interrupt extends ZIOAppDefault {
  def task = {
    for {
      fn <- ZIO.fiberId.map(_.threadName)
      _ <- ZIO.debug(s"$fn starts a long running task")
      _ <- ZIO.sleep(1.minute)
      _ <- ZIO.debug("done!")
    } yield ()
  }

  def run =
    for {
      f <-
        task
          .onInterrupt(
            ZIO.debug(s"Task interrupted while running")
          )
          .fork
      _ <- f.interrupt
    } yield ()
}
