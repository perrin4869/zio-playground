package il.co.dotcore.zio.playground.core.zioapp

import zio._

object FinalizerCompletesWithinTimeout extends ZIOAppDefault {
  // Wait at most 30 seconds for all finalizers to complete on SIGINT
  override def gracefulShutdownTimeout: Duration = 30.seconds

  val run: ZIO[ZIOAppArgs with Scope, Any, Any] =
    ZIO.acquireReleaseWith(
      acquire = ZIO.logInfo("Acquiring resource...").as("MyResource")
    )(release =
      _ =>
        ZIO.logInfo("Releasing resource (3s) ...") *> ZIO.sleep(3.seconds) *>
          ZIO.logInfo("Cleanup done")
    ) { resource =>
      ZIO.logInfo(
        s"Running with $resource, press Ctrl+C to interrupt"
      ) *> ZIO.never
    }
}
