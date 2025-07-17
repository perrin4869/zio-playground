package il.co.dotcore.zio.playground.stm

import zio._
import zio.stm._

object TPromiseExamples extends ZIOAppDefault {

  val tPromise: STM[Nothing, TPromise[String, Int]] = TPromise.make[String, Int]

  val tPromiseSucceed: UIO[TPromise[String, Int]] = for {
    tPromise <- TPromise.make[String, Int].commit
    _ <- tPromise.succeed(0).commit
  } yield tPromise

  val tPromiseFail: UIO[TPromise[String, Int]] = for {
    tPromise <- TPromise.make[String, Int].commit
    _ <- tPromise.fail("failed").commit
  } yield tPromise

  val tPromiseDoneSucceed: UIO[TPromise[String, Int]] = for {
    tPromise <- TPromise.make[String, Int].commit
    _ <- tPromise.done(Right(0)).commit
  } yield tPromise

  val tPromiseDoneFail: UIO[TPromise[String, Int]] = for {
    tPromise <- TPromise.make[String, Int].commit
    _ <- tPromise.done(Left("failed")).commit
  } yield tPromise

  val tPromiseOptionValue: UIO[Option[Either[String, Int]]] = for {
    tPromise <- TPromise.make[String, Int].commit
    _ <- tPromise.succeed(0).commit
    res <- tPromise.poll.commit
  } yield res

  val tPromiseValue: IO[String, Int] = for {
    tPromise <- TPromise.make[String, Int].commit
    _ <- tPromise.succeed(0).commit
    res <- tPromise.await.commit
  } yield res

  val program = for {
    _ <- tPromiseSucceed.flatMap(_.ref.get.commit).debug("tPromiseSucceed")
    _ <- tPromiseFail.flatMap(_.ref.get.commit).debug("tPromiseFail")
    _ <- tPromiseDoneSucceed
      .flatMap(_.ref.get.commit)
      .debug("tPromiseDoneSucceed")
    _ <- tPromiseDoneFail.flatMap(_.ref.get.commit).debug("tPromiseDoneFail")
    // seems .poll is equivalent to .ref.get
    _ <- tPromiseOptionValue.debug("tPromiseOptionValue")
    _ <- tPromiseValue.debug("tPromiseValue")
  } yield ()

  def run = program.debug
}
