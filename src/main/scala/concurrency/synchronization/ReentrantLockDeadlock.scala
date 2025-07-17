package il.co.dotcore.zio.playground

import zio._
import zio.concurrent._

object ReentrantLockDeadlock extends ZIOAppDefault {
  def workflow1(l1: ReentrantLock, l2: ReentrantLock) =
    for {
      f <- ZIO.fiberId.map(_.threadName)
      _ <- l1.lock *> ZIO.debug(s"$f locked the l1")
      o <- l2.owner.map(_.map(_.threadName))
      _ <- ZIO.debug(s"$f trying to lock the l2 while the $o is its owner") *>
        l2.lock *>
        ZIO.debug(s"$f locked the l2")
      _ <- l2.unlock
      _ <- l1.unlock
    } yield ()

  def workflow2(l1: ReentrantLock, l2: ReentrantLock) =
    for {
      f <- ZIO.fiberId.map(_.threadName)
      _ <- l2.lock *> ZIO.debug(s"$f locked the l2")
      o <- l1.owner.map(_.map(_.threadName))
      _ <- ZIO.debug(s"$f trying to lock the l1 while the $o is its owner") *>
        l1.lock *>
        ZIO.debug(s"$f locked the l1")
      _ <- l1.unlock
      _ <- l2.unlock
    } yield ()

  def run =
    for {
      l1 <- ReentrantLock.make()
      l2 <- ReentrantLock.make()
      _ <- workflow1(l1, l2) <&> workflow2(l1, l2)
    } yield ()
}
