package il.co.dotcore.zio.playground.streaming.chunk

import zio._
import zio.Console._

object ChunkExamples extends ZIOAppDefault {

  val emptyChunk = Chunk.empty
  val specifiedValuesChunk = Chunk(1, 2, 3)
  val fromIterableChunk: Chunk[Int] = Chunk.fromIterable(List(1, 2, 3))
  val fromArrayChunk: Chunk[Int] = Chunk.fromArray(Array(1, 2, 3))
  val chunk: Chunk[Int] = Chunk.fill(3)(0)
  val unfolded = Chunk.unfold(0)(n => if (n < 8) Some((n * 2, n + 2)) else None)
  val collectChunk =
    Chunk("Hello ZIO", 1.5, "Hello ZIO NIO", 2.0, "Some string", 2.5)

  override def run = for {
    _ <- ZIO.succeed(emptyChunk).debug("emptyChunk")
    _ <- ZIO.succeed(specifiedValuesChunk).debug("specifiedValuesChunk")
    _ <- ZIO.succeed(fromIterableChunk).debug("fromIterableChunk")
    _ <- ZIO.succeed(fromArrayChunk).debug("fromArrayChunk")
    _ <- ZIO.succeed(chunk).debug("chunk")
    _ <- ZIO.succeed(unfolded).debug("unfolded")
    _ <- ZIO
      .succeed(Chunk(1, 2, 3) ++ Chunk(4, 5, 6))
      .debug("Chunk(1,2,3) ++ Chunk(4,5,6)")
    _ <- ZIO.succeed(collectChunk).debug("collectChunk")
    _ <- ZIO
      .succeed(collectChunk.collect { case string: String => string })
      .debug("collectChunk.collect { case string: String => string }")
    _ <- ZIO
      .succeed(collectChunk.collect { case digit: Double => digit })
      .debug("collectChunk.collect { case digit: Double => digit }")
    _ <- ZIO
      .succeed(Chunk("Sarah", "Bob", "Jane").collectWhile {
        case element if element != "Bob" => true
      })
      .debug(
        "Chunk(\"Sarah\", \"Bob\", \"Jane\").collectWhile { case element if element != \"Bob\" => true }"
      )
    _ <- ZIO
      .succeed(Chunk("Sarah", "Bob", "Jane").drop(1))
      .debug("Chunk(\"Sarah\", \"Bob\", \"Jane\").drop(1)")
    _ <- ZIO
      .succeed(Chunk(9, 2, 5, 1, 6).dropWhile(_ >= 2))
      .debug("Chunk(9, 2, 5, 1, 6).dropWhile(_ >= 2)")
    _ <- ZIO
      .succeed(Chunk("A", "B") == Chunk("A", "C"))
      .debug("Chunk(\"A\",\"B\") == Chunk(\"A\", \"C\")")
    _ <- ZIO.succeed(Chunk(1, 2, 3).toArray).debug("Chunk(1,2,3).toArray")
    _ <- ZIO.succeed(Chunk(1, 2, 3).toSeq).debug("Chunk(1,2,3).toSeq")
  } yield ()
}
