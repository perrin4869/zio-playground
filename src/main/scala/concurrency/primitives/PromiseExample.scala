package il.co.dotcore.zio.playground.concurrency.primitives

import zio._
import java.io.IOException

object PromiseExample extends ZIOAppDefault {

  val program: ZIO[Any, IOException, Unit] =
    for {
      promise <- Promise.make[Nothing, String]
      // _ <- promise.poll.debug("POLLING")
      sendHelloWorld = (ZIO.succeed("hello world") <* ZIO.sleep(1.second))
        .flatMap(promise.succeed)
      getAndPrint = promise.await.flatMap(Console.printLine(_))
      fiberA <- sendHelloWorld.fork
      fiberB <- getAndPrint.fork
      _ <- (fiberA zip fiberB).join
      // _ <- promise.poll.debug("POLLING")
    } yield ()

  override def run = program
}
