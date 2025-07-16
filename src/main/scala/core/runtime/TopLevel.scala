package il.co.dotcore.myziotest

import zio._

object TopLevel extends ZIOAppDefault {

  // In a real-world application we might need to implement a `sl4jlogger` layer
  val addSimpleLogger: ZLayer[Any, Nothing, Unit] =
    Runtime.addLogger((_, _, _, message: () => Any, _, _, _, _) =>
      println(message())
    )

  val layer: ZLayer[Any, Nothing, Unit] =
    Runtime.removeDefaultLoggers ++ addSimpleLogger

  override val runtime: Runtime[Any] =
    Unsafe.unsafe { implicit unsafe =>
      Runtime.unsafe.fromLayer(layer)
    }

  def run = ZIO.log("Application started!")
}
