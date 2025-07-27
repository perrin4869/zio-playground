package il.co.dotcore.zio.playground.streaming.zchannel.creating

import zio._
import zio.stream._

object Fail extends ZIOAppDefault {

  val channel: ZChannel[Any, Any, Any, Any, Exception, Nothing, Nothing] =
    ZChannel.fail(new Exception("error"))

  override def run = for {
    _ <- channel.runCollect.cause.debug
  } yield ()
}
