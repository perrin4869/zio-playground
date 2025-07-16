package il.co.dotcore.myziotest

import zio._

object Bootstrap extends ZIOAppDefault {
  val addSimpleLogger: ZLayer[Any, Nothing, Unit] =
    Runtime.addLogger((_, _, _, message: () => Any, _, _, _, _) =>
      println(message())
    )

  override val bootstrap: ZLayer[Any, Nothing, Unit] =
    Runtime.removeDefaultLoggers ++ addSimpleLogger

  def run =
    for {
      _ <- ZIO.log("Application started!")
      _ <- ZIO.log("Application is about to exit!")
    } yield ()
}
