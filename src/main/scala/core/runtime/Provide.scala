package il.co.dotcore.zio.playground

import zio._

object Provide extends ZIOAppDefault {
  val addSimpleLogger: ZLayer[Any, Nothing, Unit] =
    Runtime.addLogger((_, _, _, message: () => Any, _, _, _, _) =>
      println(message())
    )

  def run =
    for {
      _ <- ZIO.log("Application started!")
      _ <- {
        for {
          _ <- ZIO.log("I'm not going to be logged!")
          _ <- ZIO
            .log("I will be logged by the simple logger.")
            .provide(addSimpleLogger)
          _ <- ZIO.log(
            "Reset back to the previous configuration, so I won't be logged."
          )
        } yield ()
      }.provide(Runtime.removeDefaultLoggers)
      _ <- ZIO.log("Application is about to exit!")
    } yield ()
}
