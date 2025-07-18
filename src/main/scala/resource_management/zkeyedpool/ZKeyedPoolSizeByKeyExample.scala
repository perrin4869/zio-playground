package il.co.dotcore.zio.playground.resource_management.zkeyedpool

import zio._

object ZKeyedPoolSizeByKeyExample extends ZIOAppDefault {
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
      pool <- ZKeyedPool.make(
        resource,
        (key: String) =>
          key match {
            case k if k.startsWith("foo") => 2
            case k if k.startsWith("bar") => 3
            case _                        => 1
          }
      )
      _ <- pool.get("foo1")
      item <- pool.get("bar1")
      _ <- ZIO.debug(s"Item: $item")
    } yield ()
}
