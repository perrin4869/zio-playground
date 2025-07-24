package il.co.dotcore.zio.playground.streaming.zstream

import zio._
import zio.Console._
import zio.stream._
import scala.io.Source
import java.io.IOException
import java.nio.file.Path
import java.nio.file.Paths

object ResourcefulStreams extends ZIOAppDefault {

  val lines: ZStream[Any, Throwable, String] =
    ZStream
      .acquireReleaseWith(
        ZIO.attempt(Source.fromFile("file.txt")) <* printLine(
          "The file was opened."
        )
      )(x => ZIO.succeed(x.close()) <* printLine("The file was closed.").orDie)
      .flatMap { is =>
        ZStream.fromIterator(is.getLines())
      }

  def application: ZStream[Any, IOException, Unit] =
    ZStream.fromZIO(printLine("Application Logic."))
  def deleteDir(dir: Path): ZIO[Any, IOException, Unit] = printLine(
    "Deleting file."
  )

  val myApp: ZStream[Any, IOException, Any] =
    application ++ ZStream.finalizer(
      (deleteDir(Paths.get("tmp")) *>
        printLine("Temporary directory was deleted.")).orDie
    )

  override def run = for {
    _ <- print("lines: ") *> lines
      .runForeachChunk(printLine(_))
    _ <- print("myApp: ") *> myApp
      .runForeachChunk(printLine(_))
    _ <- print("ensuring: ") *> ZStream
      .finalizer(Console.printLine("Finalizing the stream").orDie)
      .ensuring(
        printLine("Doing some other works after stream's finalization").orDie
      )
      .runForeachChunk(printLine(_))
  } yield ()
}
