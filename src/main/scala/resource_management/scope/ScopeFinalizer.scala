package il.co.dotcore.zio.playground.resource_management.scope

import zio._

object ScopeFinalizer extends ZIOAppDefault {
  override def run = for {
    scope <- Scope.make
    _ <- ZIO.debug("Scope is created!")
    _ <- scope.addFinalizer(
      for {
        _ <- ZIO.debug("The finalizer is started!")
        _ <- ZIO.sleep(5.seconds)
        _ <- ZIO.debug("The finalizer is done!")
      } yield ()
    )
    _ <- ZIO.debug("Leaving scope!")
    _ <- scope.close(Exit.succeed(()))
    _ <- ZIO.debug("Scope is closed!")
  } yield ()
}
