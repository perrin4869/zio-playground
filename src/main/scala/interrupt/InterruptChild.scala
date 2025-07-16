package il.co.dotcore.myziotest

import zio._

object InterruptChild extends ZIOAppDefault {
  def debugInterruption(taskName: String) = (fibers: Set[FiberId]) =>
    for {
      fn <- ZIO.fiberId.map(_.threadName)
      _ <- ZIO.debug(
        s"the $fn fiber which is the underlying fiber of the $taskName task " +
          s"interrupted by ${fibers.map(_.threadName).mkString(", ")}"
      )
    } yield ()

  def run =
    for {
      fn <- ZIO.fiberId.map(_.threadName)
      _ <- ZIO.debug(s"$fn starts working.")
      child =
        for {
          cfn <- ZIO.fiberId.map(_.threadName)
          _ <- ZIO.debug(
            s"$cfn starts working by forking from its parent ($fn)"
          )
          _ <- ZIO.never
        } yield ()
      _ <- child.onInterrupt(debugInterruption("child")).fork
      _ <- ZIO.sleep(1.second)
      _ <- ZIO.debug(s"$fn finishes its job and is going go exit.")
    } yield ()
}
