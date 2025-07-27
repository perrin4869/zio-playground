package il.co.dotcore.zio.playground.streaming.subscription_ref

import zio._
import zio.Console._
import zio.stream._

object Example extends ZIOAppDefault {

  def server(ref: Ref[Long]): UIO[Nothing] =
    ref.update(_ + 1).forever

  def client(changes: ZStream[Any, Nothing, Long]): UIO[Chunk[Long]] =
    for {
      n <- Random.nextLongBetween(1, 200)
      chunk <- changes.take(n).runCollect
    } yield chunk

  override def run = for {
    subscriptionRef <- SubscriptionRef.make(0L)
    server <- server(subscriptionRef).fork
    chunks <- ZIO.collectAllPar(List.fill(100)(client(subscriptionRef.changes)))
    _ <- server.interrupt
    _ <- ZIO.foreach(chunks)(chunk => Console.printLine(chunk))
  } yield ()
}
