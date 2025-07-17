package il.co.dotcore.zio.playground

import zio._

object FinalizerExceedsTimeout extends ZIOAppDefault {
  // Wait at most 5 seconds for finalizers to complete on SIGINT
  override def gracefulShutdownTimeout: Duration = 5.seconds

  val run: ZIO[ZIOAppArgs with Scope, Any, Any] =
    ZIO.acquireReleaseWith(
      acquire = ZIO.logInfo("Acquiring resource...").as("MyResource")
    )(release =
      _ =>
        ZIO.logInfo("Releasing resource (20s) ...") *> ZIO.sleep(20.seconds) *>
          ZIO.logInfo("Cleanup done")
    ) { resource =>
      ZIO.logInfo(
        s"Running with $resource, press Ctrl+C to interrupt"
      ) *> ZIO.never
    }
}
