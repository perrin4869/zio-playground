package il.co.dotcore.zio.playground.streaming.zstream.operations

import zio._
import zio.stream._
import zio.Console._

object Collecting extends ZIOAppDefault {
  val source1 = ZStream(1, 2, 3, 4, 0, 5, 6, 7, 8)

  val s1 = source1.collect { case x if x < 6 => x * 2 }
  // Output: 2, 4, 6, 8, 0, 10

  val s2 = source1.collectWhile { case x if x != 0 => x * 2 }
  // Output: 2, 4, 6, 8

  val source2 = ZStream(Left(1), Right(2), Right(3), Left(4), Right(5))

  val s3 = source2.collectLeft
  // Output: 1, 4

  val s4 = source2.collectWhileLeft
  // Output: 1

  val s5 = source2.collectRight
  // Output: 2, 3, 5

  val s6 = source2.drop(1).collectWhileRight
  // Output: 2, 3

  val s7 = source2.map(_.toOption).collectSome
  // Output: 2, 3, 5

  val s8 = source2.map(_.toOption).collectWhileSome
  // Output: empty stream

  val urls = ZStream(
    "dotty.epfl.ch",
    "zio.dev",
    "zio.github.io/zio-json",
    "zio.github.io/zio-nio/"
  )

  def fetch(url: String): ZIO[Any, Throwable, String] =
    // ZIO.attemptBlocking(???)
    ZIO.attemptBlocking(
      if (url.contains("dev")) throw new Exception("dev")
      else s"Download($url)"
    )

  val pages = urls
    .mapZIO(url => fetch(url).exit)
    .collectSuccess

  override def run = for {
    _ <- print("s1: ") *> s1
      .runForeachChunk(printLine(_)) *> print("\n")
    _ <- print("s2: ") *> s2
      .runForeachChunk(printLine(_))
    _ <- print("s3: ") *> s3
      .runForeachChunk(printLine(_))
    _ <- print("s4: ") *> s4
      .runForeachChunk(printLine(_))
    _ <- print("s5: ") *> s5
      .runForeachChunk(printLine(_))
    _ <- print("s6: ") *> s6
      .runForeachChunk(printLine(_))
    _ <- print("s7: ") *> s7
      .runForeachChunk(printLine(_))
    _ <- print("s8: ") *> s8
      .runForeachChunk(printLine(_))
    _ <- print("pages: ") *> pages
      .runForeachChunk(printLine(_))
  } yield ()
}
