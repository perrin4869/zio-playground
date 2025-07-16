package il.co.dotcore.myziotest

import zio._

object ForkScoped extends ZIOAppDefault {
  val barJob: ZIO[Any, Nothing, Long] =
    ZIO
      .debug("Bar: still running!")
      .repeat(Schedule.fixed(1.seconds))

  val fooJob: ZIO[Scope, Nothing, Unit] =
    (for {
      _ <- ZIO.debug("Foo: started!")
      _ <- barJob.forkScoped
      _ <- ZIO.sleep(2.seconds)
      _ <- ZIO.debug("Foo: finished!")
    } yield ()).onInterrupt(_ => ZIO.debug("Foo: interrupted!"))

  def run =
    for {
      _ <- ZIO.scoped {
        for {
          _ <- ZIO.debug("Local scope started!")
          _ <- fooJob.fork
          _ <- ZIO.sleep(5.seconds)
          _ <- ZIO.debug("Leaving the local scope!")
        } yield ()
      }
      _ <- ZIO.debug("Do something else and sleep for 10 seconds")
      _ <- ZIO.sleep(10.seconds)
      _ <- ZIO.debug("Application exited!")
    } yield ()
}
