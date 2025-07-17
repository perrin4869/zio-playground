package il.co.dotcore.zio.playground.concurrency.primitives

import zio._
import zio.stream.ZStream

object HubExample extends ZIOAppDefault {
  val res1 = Hub.bounded[String](2).flatMap { hub =>
    ZIO.scoped {
      hub.subscribe.zip(hub.subscribe).flatMap { case (left, right) =>
        for {
          _ <- hub.publish("Hello from a hub!")
          _ <- left.take.flatMap(Console.printLine(_))
          _ <- right.take.flatMap(Console.printLine(_))
        } yield ()
      }
    }
  }

  val res2 = for {
    promise <- Promise.make[Nothing, Unit]
    hub <- Hub.bounded[String](2)
    scoped = ZStream.fromHubScoped(hub).tap(_ => promise.succeed(()))
    stream = ZStream.unwrapScoped(scoped)
    fiber <- stream.take(2).runCollect.fork
    _ <- promise.await
    _ <- hub.publish("Hello")
    _ <- hub.publish("World")
    strs <- fiber.join
  } yield strs

  // override def run = res1.debug
  override def run = res2.debug
}
