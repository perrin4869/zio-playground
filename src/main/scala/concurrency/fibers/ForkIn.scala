package il.co.dotcore.zio.playground

import zio._

object ForkIn extends ZIOAppDefault {
  def run =
    ZIO.scoped {
      for {
        scope <- ZIO.scope
        _ <-
          ZIO.scoped {
            for {
              _ <- ZIO
                .debug("Still running ...")
                .repeat(Schedule.fixed(1.second))
                .forkIn(scope)
              _ <- ZIO.sleep(3.seconds)
              _ <- ZIO.debug("The innermost scope is about to be closed.")
            } yield ()
          }
        _ <- ZIO.sleep(5.seconds)
        _ <- ZIO.debug("The outer scope is about to be closed.")
      } yield ()
    }
}
