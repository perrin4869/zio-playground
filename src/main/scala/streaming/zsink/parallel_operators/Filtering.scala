package il.co.dotcore.zio.playground.streaming.zsink.parallel_operators

import zio._
import zio.stream._
import zio.Console._

object Zipping extends ZIOAppDefault {

  val kafkaSink: ZSink[Any, Throwable, Record, Record, Unit] =
    // ZSink.foreach[Any, Throwable, Record](record => ZIO.attempt(???))
    ZSink.foreach[Any, Throwable, Record](record =>
      ZIO.attempt(println(s"kafka($record)"))
    )

  val pulsarSink: ZSink[Any, Throwable, Record, Record, Unit] =
    // ZSink.foreach[Any, Throwable, Record](record => ZIO.attempt(???))
    ZSink.foreach[Any, Throwable, Record](record =>
      ZIO.attempt(println(s"pulsar($record)"))
    )

  val stream: ZSink[Any, Throwable, Record, Record, Unit] =
    kafkaSink zipPar pulsarSink

  case class MyRecord(id: Int) extends Record
  override def run = for {
    _ <- ZStream(MyRecord(10)).run(stream)
  } yield ()
}
