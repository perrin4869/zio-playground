package il.co.dotcore.zio.playground

import zio._
import zio.Console._

object TestTest extends ZIOAppDefault {
  def run = for {
    f1 <- (ZIO.sleep(10.seconds) *> ZIO.debug("MY TASK")).fork
    _ <- f1.status.debug("fiber running ZIO.sleep status")
    _ <- printLine("GOING TO SLEEP NOW BYE")
    _ <- ZIO.sleep(1.seconds)
    _ <- f1.status
      .debug("fiber running ZIO.sleep status")
    _ <- f1.join
  } yield ()
}
