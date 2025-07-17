package il.co.dotcore.zio.playground

import zio._

import zio._
import java.util.concurrent.atomic.AtomicReference

final case class BlockingService() {
  private val released = new AtomicReference(false)

  def start(): Unit = {
    while (!released.get()) {
      println("Doing some blocking operation")
      try Thread.sleep(1000)
      catch {
        case _: InterruptedException => () // Swallowing InterruptedException
      }
    }
    println("Blocking operation closed.")
  }

  def close(): Unit = {
    println("Releasing resources and ready to be closed.")
    released.getAndSet(true)
  }
}

object InterruptService extends ZIOAppDefault {
  def run = for {
    service <- ZIO.attempt(BlockingService())
    fiber <- ZIO
      .attemptBlockingCancelable(
        effect = service.start()
      )(
        cancel = ZIO.succeed(service.close())
      )
      .fork
    _ <- fiber.interrupt.schedule(
      Schedule.delayed(
        Schedule.duration(3.seconds)
      )
    )
  } yield ()
}
