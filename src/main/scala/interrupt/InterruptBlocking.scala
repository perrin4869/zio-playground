package il.co.dotcore.zio.playground.interrupt

import zio._

object InterruptBlocking extends ZIOAppDefault {

  // def run = for {
  //   _ <- Console.printLine("Starting a blocking operation")
  //   fiber <- ZIO
  //     .attemptBlocking {
  //       while (true) {
  //         Thread.sleep(1000)
  //         println("Doing some blocking operation")
  //       }
  //     }
  //     .ensuring(
  //       Console.printLine("End of a blocking operation").orDie
  //     )
  //     .fork
  //   _ <- fiber.interrupt.schedule(
  //     Schedule.delayed(
  //       Schedule.duration(1.seconds)
  //     )
  //   )
  // } yield ()

  def run =
    for {
      _ <- Console.printLine("Starting a blocking operation")
      fiber <- ZIO
        .attemptBlockingInterrupt {
          while (true) {
            Thread.sleep(1000)
            println("Doing some blocking operation")
          }
        }
        .ensuring(
          Console.printLine("End of the blocking operation").orDie
        )
        .fork
      _ <- fiber.interrupt.schedule(
        Schedule.delayed(
          Schedule.duration(3.seconds)
        )
      )
    } yield ()
}
