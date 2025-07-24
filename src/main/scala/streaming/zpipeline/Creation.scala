package il.co.dotcore.zio.playground.streaming.zpipeline

import zio._
import zio.Console._
import zio.stream._

object Creation extends ZIOAppDefault {

  val stream = ZStream("foo", "bar", "baz")
  val chars =
    ZPipeline.map[String, Chunk[Char]](s => Chunk.fromArray(s.toArray)) >>>
      ZPipeline.mapChunks[Chunk[Char], Char](_.flatten)

  override def run = for {
    _ <- print("stream: ") *> stream.via(chars).foreach(printLine(_))
  } yield ()
}
