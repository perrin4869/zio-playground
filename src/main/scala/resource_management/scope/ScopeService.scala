package il.co.dotcore.zio.playground.resource_management.scope

import zio._

object ScopeService extends ZIOAppDefault {
  val resourcefulApp: ZIO[Scope, Nothing, Unit] =
    for {
      scope <- ZIO.service[Scope]
      _ <- ZIO.debug("Entering the scope!")
      _ <- scope.addFinalizer(
        for {
          _ <- ZIO.debug("The finalizer is started!")
          _ <- ZIO.sleep(5.seconds)
          _ <- ZIO.debug("The finalizer is done!")
        } yield ()
      )
      _ <- ZIO.debug("Leaving scope!")
    } yield ()

  val finalApp: ZIO[Any, Nothing, Unit] =
    Scope.make.flatMap(scope =>
      resourcefulApp.provide(ZLayer.succeed(scope)).onExit(scope.close(_))
    )

  override def run = finalApp
  // this works too because ZIOAppDefault also provides a scope
  // override def run = resourcefulApp
}
