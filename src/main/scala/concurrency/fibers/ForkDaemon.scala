package il.co.dotcore.zio.playground

import zio._

object ForkDaemon extends ZIOAppDefault {
  val barJob: ZIO[Any, Nothing, Long] =
    ZIO
      .debug("Bar: still running!")
      .repeat(Schedule.fixed(1.seconds))

  val fooJob: ZIO[Any, Nothing, Unit] =
    for {
      _ <- ZIO.debug("Foo: started!")
      _ <- barJob.forkDaemon
      _ <- ZIO.sleep(3.seconds)
      _ <- ZIO.debug("Foo: finished!")
    } yield ()

  def run =
    for {
      f <- fooJob.fork
      _ <- ZIO.sleep(5.seconds)
    } yield ()
}
