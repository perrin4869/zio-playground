package il.co.dotcore.zio.playground.resource_management.scope

import zio._

object ExtendingScopesExample extends ZIOAppDefault {
  val resource1: ZIO[Scope, Nothing, Unit] =
    ZIO.acquireRelease(ZIO.debug("Acquiring the resource 1"))(_ =>
      ZIO.debug("Releasing the resource one") *> ZIO.sleep(5.seconds)
    )
  val resource2: ZIO[Scope, Nothing, Unit] =
    ZIO.acquireRelease(ZIO.debug("Acquiring the resource 2"))(_ =>
      ZIO.debug("Releasing the resource two") *> ZIO.sleep(3.seconds)
    )

  def run =
    ZIO.scoped(
      for {
        scope <- ZIO.scope
        _ <- ZIO.debug("Entering the main scope!")
        _ <- scope.addFinalizer(
          ZIO.debug("Releasing the main resource!") *> ZIO.sleep(2.seconds)
        )
        _ <- scope.extend(resource1)
        _ <- scope.extend(resource2)
        _ <- ZIO.debug("Leaving scope!")
      } yield ()
    )

}
