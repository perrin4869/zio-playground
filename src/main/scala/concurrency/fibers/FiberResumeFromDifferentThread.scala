package il.co.dotcore.zio.playground

import zio._
import java.util.concurrent.Executors

object FiberResumeFromDifferentThread extends ZIOAppDefault {
  val executor = Executors.newSingleThreadExecutor()

  def asyncOperation: ZIO[Any, Throwable, String] =
    ZIO.async { callback =>
      executor.submit(new Runnable {
        def run(): Unit = {
          println(
            s"[callback thread] Completing async op on ${Thread.currentThread().getName}"
          )
          callback(ZIO.succeed("Hello from async"))
        }
      })
    }

  val asyncEffect: ZIO[Any, Nothing, Unit] = ZIO.async { cb =>
    executor.submit(new Runnable {
      def run(): Unit = {
        println(
          s"[callback thread] Completing async on ${Thread.currentThread().getName}"
        )
        cb(ZIO.debug(s"Resuming... on ${Thread.currentThread().getName}"))
      }
    })
  }

  def run =
    for {
      _ <- ZIO.debug(s"Start on ${Thread.currentThread().getName}")
      // msg <- asyncOperation
      _ <- asyncEffect
      _ <- ZIO.debug(s"Done on ${Thread.currentThread().getName}")
      // _ <- ZIO.debug(
      //   s"Resumed on ${Thread.currentThread().getName} with message: $msg"
      // )
    } yield ()
}
