package il.co.dotcore.zio.playground.streaming.zstream.error_handling

import zio._
import zio.stream._

object FromToEither extends ZIOAppDefault {
  import java.net.URL
  import java.io.IOException

  // def legacyFetchUrlAPI(url: URL): Either[Throwable, String] = ???
  def legacyFetchUrlAPI(url: URL): Either[Throwable, String] = Right(
    s"Downloaded($url)"
  )

  def fetchUrl(
      url: URL
  ): ZStream[Any, Throwable, String] =
    ZStream
      .fromZIO(
        ZIO.attemptBlocking(legacyFetchUrlAPI(url))
      )
      .absolve

  val inputs: ZStream[Any, Nothing, Either[IOException, String]] =
    ZStream.fromZIO(Console.readLine).either

  // Stream of Either values that cannot fail
  val eitherStream: ZStream[Any, Nothing, Either[String, Int]] =
    ZStream(Right(1), Right(2), Left("failed to parse"), Right(4))

  // A Fails with the first emission of the left value
  val stream: ZStream[Any, String, Int] = eitherStream.rightOrFail("fail")

  override def run = for {
    _ <- Console.print("fetchUrl: ") *>
      fetchUrl(new URL("https://foo.com"))
        .runForeachChunk(Console.printLine(_)) *> Console.print("\n")
    _ <- Console.print("inputs: ") *>
      inputs.runForeachChunk(Console.printLine(_)) *> Console.print("\n")
    _ <- Console.print("stream: ") *>
      stream.runForeachChunk(Console.printLine(_)) *> Console.print("\n")
  } yield ()
}
