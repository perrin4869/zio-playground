package il.co.dotcore.zio.playground

import zio._
import java.io.IOException

object SuspendedEffect extends ZIOAppDefault {

  def run = myAppLogic

  val suspendedEffect: RIO[Any, ZIO[Any, IOException, Unit]] =
    ZIO.suspend(ZIO.attempt(Console.printLine("Suspended Hello World!")))
  // ZIO.attempt(Console.printLine("Suspended Hello World!"))

  // val suspendedEffect =
  //   ZIO.suspend({
  //     println("OMG")
  //     Console.printLine("Suspended Hello World!")
  //   })

  val myAppLogic = for {
    // _ <- Console.printLine("Suspended Hello World!")
    effect <- suspendedEffect
    _ <- effect
  } yield ()
  // val myAppLogic = Console.printLine("Suspended Hello World!")
}
