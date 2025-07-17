package il.co.dotcore.zio.playground.stm

import zio._
import zio.stm._

object TRandomExamples extends ZIOAppDefault {

  val program = for {
    _ <- TRandom.nextInt.commit.debug
  } yield ()

  def run = program.debug.provide(TRandom.live)
}
