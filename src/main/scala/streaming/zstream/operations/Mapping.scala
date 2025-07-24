package il.co.dotcore.zio.playground.streaming.zstream.operations

import zio._
import zio.stream._
import zio.Console._

object Mapping extends ZIOAppDefault {
  val intStream: UStream[Int] = ZStream.fromIterable(0 to 100)
  val stringStream: UStream[String] = intStream.map(_.toString)

  import java.net.URL
  // def fetchUrl(url: URL): Task[String] = ZIO.succeed(???)
  // def getUrls: Task[List[URL]] = ZIO.succeed(???)
  def fetchUrl(url: URL): Task[String] =
    ZIO.succeed(s"Download(${url.toString()})")
  def getUrls: Task[List[URL]] = ZIO.succeed(
    List(
      new URL("https://foo.com"),
      new URL("https://bar.com"),
      new URL("https://baz.com")
    )
  )

  val pages = ZStream.fromIterableZIO(getUrls).mapZIOPar(8)(fetchUrl)

  val chunked =
    ZStream
      .fromChunks(Chunk(1, 2, 3), Chunk(4, 5), Chunk(6, 7, 8, 9))
  val stream = chunked.mapChunks(x => x.tail)

  def runningTotal(stream: UStream[Int]): UStream[Int] =
    stream.mapAccum(0)((acc, next) => (acc + next, acc + next))

  val numbers: UStream[Int] =
    ZStream("1-2-3", "4-5", "6")
      .mapConcat(_.split("-"))
      .map(_.toInt)

  val unitStream: ZStream[Any, Nothing, Unit] =
    ZStream.range(1, 5).as(())

  override def run = for {
    _ <- print("Mapping.stringStream: ") *> Mapping.stringStream
      .runForeachChunk(printLine(_))
    _ <- print("Mapping.pages: ") *> Mapping.pages
      .runForeachChunk(printLine(_))
    _ <- print("Mapping.stream: ") *> Mapping.stream
      .runForeachChunk(printLine(_))
    _ <- print("Mapping.runningTotal(ZIO.fromIterable(0 to 5)): ") *> Mapping
      .runningTotal(ZStream.fromIterable(0 to 5))
      .runForeachChunk(printLine(_))
    _ <- print("Mapping.numbers: ") *> Mapping.numbers
      .runForeachChunk(printLine(_))
    _ <- print("Mapping.unitStream: ") *> Mapping.unitStream
      .runForeachChunk(printLine(_))
  } yield ()
}
