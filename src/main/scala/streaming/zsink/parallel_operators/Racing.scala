package il.co.dotcore.zio.playground.streaming.zsink.parallel_operators

import zio._
import zio.stream._
import zio.Console._

object Racing extends ZIOAppDefault {

  val kafkaSink: ZSink[Any, Throwable, Nothing, Nothing, String] =
    ZSink.succeed("kafka")

  val pulsarSink: ZSink[Any, Throwable, Nothing, Nothing, String] =
    ZSink.succeed("pulsar")

  val stream: ZSink[Any, Throwable, Nothing, Nothing, String] =
    kafkaSink race pulsarSink

  override def run = for {
    _ <- ZStream().run(stream).debug
  } yield ()
}
