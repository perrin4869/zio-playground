package il.co.dotcore.zio.playground.core.runtime

import zio._
import zio.Executor
import java.util.concurrent.{LinkedBlockingQueue, ThreadPoolExecutor, TimeUnit}

object CustomizedRuntime extends ZIOAppDefault {
  override val bootstrap = Runtime.setExecutor(
    Executor.fromThreadPoolExecutor(
      new ThreadPoolExecutor(
        5,
        10,
        5000,
        TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue[Runnable]()
      )
    )
  )

  def run = myAppLogic

  val myAppLogic =
    for {
      _ <- Console.printLine("Hello! What is your name?")
      name <- Console.readLine
      _ <- Console.printLine(s"Hello, ${name}, welcome to ZIO!")
    } yield ()
}
