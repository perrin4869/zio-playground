package il.co.dotcore.zio.playground.resource_management.zpool

import zio._
import java.util.UUID

object DynamicPoolExample extends ZIOAppDefault {
  def resource: ZIO[Scope, Nothing, UUID] = ZIO.acquireRelease(
    ZIO.random
      .flatMap(_.nextUUID)
      .flatMap(uuid => ZIO.debug(s"Acquiring the resource: $uuid!").as(uuid))
  )(uuid => ZIO.debug(s"Releasing the resource $uuid!"))

  def run =
    for {
      pool <- ZPool.make(resource, 10 to 20, 60.seconds)
      item <- pool.get
      _ <- ZIO.debug(s"Item: $item")
    } yield ()
}
