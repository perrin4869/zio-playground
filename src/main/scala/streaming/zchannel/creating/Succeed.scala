package il.co.dotcore.zio.playground.streaming.zchannel.creating

import zio._
import zio.stream._

object Succeed extends ZIOAppDefault {
  val channel: ZChannel[Any, Any, Any, Any, Nothing, Nothing, Int] =
    ZChannel.succeed(42)

  override def run = for {
    _ <- channel.runCollect.debug
  } yield ()
}
