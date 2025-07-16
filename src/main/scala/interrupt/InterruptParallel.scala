package il.co.dotcore.myziotest

import zio._

object InterruptParallel extends ZIOAppDefault {
  def debugInterruption(taskName: String) = (fibers: Set[FiberId]) =>
    for {
      fn <- ZIO.fiberId.map(_.threadName)
      _ <- ZIO.debug(
        s"The $fn fiber which is the underlying fiber of the $taskName task " +
          s"interrupted by ${fibers.map(_.threadName).mkString(", ")}"
      )
    } yield ()

  def task[R, E, A](name: String)(zio: ZIO[R, E, A]): ZIO[R, E, A] =
    zio.onInterrupt(debugInterruption(name))

  def debugMainFiber =
    for {
      fn <- ZIO.fiberId.map(_.threadName)
      _ <- ZIO.debug(
        s"Main fiber ($fn) starts executing the whole application."
      )
    } yield ()

  def run = {
    // start interrupting fiber
    val first = task("first")(ZIO.interrupt)

    // never ending fiber
    val second = task("second")(ZIO.never)

    debugMainFiber *> {
      // uncomment each line and run the code to see the result

      // first fiber will be interrupted
      first *> second

      // never ending application
      // second *> first

      // first fiber will be interrupted
      // first <*> second

      // never ending application
      // second <*> first

      // first and second will be interrupted
      // first <&> second
      //
      // first and second will be interrupted
      // second <&> first
    }
    // }.debug
  }
}
