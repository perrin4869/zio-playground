package il.co.dotcore.zio.playground

import zio._
import zio.Console._

object CauseTest extends ZIOAppDefault {

  def myEffect = for {
    i <- ZIO.succeed(5)
    _ <- ZIO.fail("Oh uh!")
    _ <- ZIO.dieMessage("Boom!")
    _ <- ZIO.interrupt
  } yield i

  def both = for {
    f1 <- ZIO.fail("Oh uh!").fork
    f2 <- ZIO.dieMessage("Boom!").fork
    _ <- (f1 <*> f2).join
  } yield ()

  def run = for {
    _ <- ZIO.succeed(5).cause.debug
    _ <- ZIO.failCause(Cause.empty).cause.debug
    _ <- ZIO.failCause(Cause.fail("Oh uh!")).cause.debug
    _ <- myEffect.cause.debug
    _ <- ZIO.failCause(Cause.die(new Throwable("Boom!"))).cause.debug
    _ <- ZIO.succeed(5 / 0).cause.debug
    _ <- ZIO.dieMessage("Boom!").cause.debug
    _ <- ZIO.interrupt.cause.debug
    _ <- ZIO.never.fork
      .flatMap(f => f.interrupt *> f.join)
      .cause
      .debug
    _ <- both.cause.debug
    _ <- (ZIO.fail("Oh uh!") <&> ZIO.dieMessage("Boom!")).cause.debug
    _ <- ZIO
      .fail("first")
      .ensuring(ZIO.die(throw new Exception("second")))
      .cause
      .debug
  } yield ()
}
