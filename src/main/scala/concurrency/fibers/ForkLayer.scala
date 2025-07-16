package il.co.dotcore.myziotest

import zio._

object ForkLayer extends ZIOAppDefault {

  val layer: ZLayer[Scope, Nothing, Int] =
    ZLayer.fromZIO {
      ZIO
        .debug("Still running ...")
        .repeat(Schedule.fixed(1.second))
        .forkDaemon
        .as(42)
    }

  override def run =
    (for {
      scope <- ZIO.scope
      _ <- ZIO
        .service[Int]
        .provideLayer(layer) *> ZIO.debug(
        "Int layer provided"
      )
      _ <- ZIO.sleep(5.seconds)
    } yield ())
}
