package il.co.dotcore.zio.playground

import zio._
import zio.concurrent._

object ReentrantLockExample extends ZIOAppDefault {

  def run =
    for {
      l <- ReentrantLock.make()
      fn <- ZIO.fiberId.map(_.threadName)
      _ <- l.lock
      _ <- ZIO.debug(s"$fn acquired the lock.")
      task =
        for {
          fn <- ZIO.fiberId.map(_.threadName)
          _ <- ZIO.debug(s"$fn attempted to acquire the lock.")
          _ <- l.lock
          _ <- ZIO.debug(s"$fn acquired the lock.")
          _ <- ZIO.debug(s"$fn will release the lock after 5 second.")
          _ <- ZIO.sleep(5.second)
          _ <- l.unlock
          _ <- ZIO.debug(s"$fn released the lock.")
        } yield ()
      f <- task.fork
      _ <- ZIO.debug(s"$fn will release the lock after 10 second.")
      _ <- ZIO.sleep(10.second)
      _ <- (l.unlock *> ZIO.debug(s"$fn released the lock.")).uninterruptible
      _ <- f.join
    } yield ()

}
