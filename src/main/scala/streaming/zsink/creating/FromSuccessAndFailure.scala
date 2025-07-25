package il.co.dotcore.zio.playground.streaming.zsink.creating

import zio._
import zio.stream._
import zio.Console._

object FromSuccessAndFailure extends ZIOAppDefault {
  object Succeed {
    val succeed: ZSink[Any, Any, Any, Nothing, Int] = ZSink.succeed(5)
  }

  object Failed {
    val failed: ZSink[Any, String, Any, Nothing, Nothing] = ZSink.fail("fail!")
  }

  override def run = for {
    _ <- ZStream().run(Succeed.succeed).debug("Succeed.succeed")
    _ <- ZStream().run(Failed.failed).cause.debug("Failed.failed")
  } yield ()
}
