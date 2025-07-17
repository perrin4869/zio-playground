package il.co.dotcore.zio.playground.interrupt

import zio._

object InterruptParent extends ZIOAppDefault {

  def debugInterruption(taskName: String) = (fibers: Set[FiberId]) =>
    for {
      fn <- ZIO.fiberId.map(_.threadName)
      _ <- ZIO.debug(
        s"The $fn fiber which is the underlying fiber of the $taskName task " +
          s"interrupted by ${fibers.map(_.threadName).mkString(", ")}"
      )
    } yield ()

  def task =
    for {
      fn <- ZIO.fiberId.map(_.threadName)
      _ <- ZIO.debug(
        s"$fn starts running that will print random numbers and booleans"
      )
      f1 <- Random
        .nextIntBounded(100)
        .debug("random number ")
        .schedule(Schedule.spaced(1.second).forever)
        .onInterrupt(debugInterruption("random number"))
        .fork
      f2 <- Random.nextBoolean
        .debug("random boolean ")
        .schedule(Schedule.spaced(2.second).forever)
        .onInterrupt(debugInterruption("random boolean"))
        .fork
      _ <- f1.join
      _ <- f2.join
    } yield ()

  def run =
    for {
      f <- task.fork
      _ <- ZIO.sleep(5.second)
      _ <- f.interrupt
    } yield ()
}
