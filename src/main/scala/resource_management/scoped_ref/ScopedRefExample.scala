package il.co.dotcore.zio.playground.resource_management.scoped_ref

import zio._

object ScopedRefExample extends ZIOAppDefault {
  def run = for {
    _ <- ZIO.unit
    r1 = ZIO.acquireRelease(
      ZIO
        .debug("acquiring the first resource")
        .as(5)
    )(_ => ZIO.debug("releasing the first resource"))
    r2 = ZIO.acquireRelease(
      ZIO
        .debug("acquiring the second resource")
        .as(10)
    )(_ => ZIO.debug("releasing the second resource"))
    sref <- ScopedRef.fromAcquire(r1)
    _ <- sref.get.debug
    _ <- sref.set(r2)
    _ <- sref.get.debug
  } yield ()
}
