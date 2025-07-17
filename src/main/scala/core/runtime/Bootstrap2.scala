package il.co.dotcore.zio.playground.core.runtime

import zio._

object Bootstrap2 extends ZIOAppDefault {
  val addSimpleLogger: ZLayer[Any, Nothing, Unit] =
    Runtime.addLogger((_, _, _, message: () => Any, _, _, _, _) =>
      println(message())
    )

  val effectfulConfiguration: ZLayer[Any, Nothing, Unit] =
    ZLayer.fromZIO(
      ZIO.log("Started effectful workflow to customize runtime configuration")
    )

  override val bootstrap: ZLayer[Any, Nothing, Unit] =
    Runtime.removeDefaultLoggers ++ addSimpleLogger ++ effectfulConfiguration

  def run =
    for {
      _ <- ZIO.log("Application started!")
      _ <- ZIO.log("Application is about to exit!")
    } yield ()
}
