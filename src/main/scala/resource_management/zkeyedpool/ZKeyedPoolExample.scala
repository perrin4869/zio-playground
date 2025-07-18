package il.co.dotcore.zio.playground.resource_management.zkeyedpool

import zio._

object ZKeyedPoolExample extends ZIOAppDefault {
  def resource(key: String): ZIO[Scope, Nothing, String] = ZIO.acquireRelease(
    ZIO.random
      .flatMap(_.nextUUID.map(_.toString))
      .flatMap(uuid =>
        ZIO
          .debug(s"Acquiring the resource with the $key key and the $uuid id")
          .as(uuid)
      )
  )(uuid =>
    ZIO.debug(s"Releasing the resource with the $key key and the $uuid id!")
  )

  def run =
    for {
      pool <- ZKeyedPool.make(resource _, 3)
      _ <- pool.get("foo")
      item <- pool.get("bar")
      _ <- ZIO.debug(s"Item: $item")
    } yield ()
}
