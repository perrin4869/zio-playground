package il.co.dotcore.zio.playground.streaming.zpipeline

import zio._
import zio.Console._
import zio.stream._

object Decompression extends ZIOAppDefault {
  import zio.stream.ZStream
  import zio.stream.ZPipeline.{gunzip, inflate, gunzipAuto}
  import zio.stream.compression.CompressionException

  def decompressDeflated(
      deflated: ZStream[Any, Nothing, Byte]
  ): ZStream[Any, CompressionException, Byte] = {
    val bufferSize: Int =
      64 * 1024 // Internal buffer size. Few times bigger than upstream chunks should work well.
    val noWrap: Boolean = false // For HTTP Content-Encoding should be false.
    deflated.via(inflate(bufferSize, noWrap))
  }

  def decompressGzipped(
      gzipped: ZStream[Any, Nothing, Byte]
  ): ZStream[Any, CompressionException, Byte] = {
    val bufferSize: Int =
      64 * 1024 // Internal buffer size. Few times bigger than upstream chunks should work well.
    gzipped.via(gunzip(bufferSize))
  }

  def decompressMaybeGzipped(
      maybeGzipped: ZStream[Any, Nothing, Byte]
  ): ZStream[Any, CompressionException, Byte] = {
    val bufferSize: Int =
      64 * 1024 // Internal buffer size. Few times bigger than upstream chunks should work well.
    maybeGzipped.via(gunzipAuto(bufferSize))
  }

  override def run = for {
    _ <- printLine(
      "Needs to first run il.co.dotcore.zio.playground.streaming.zpipeline.Compression to generate the compressed files."
    )
    _ <- print("file.zz: ") *> decompressDeflated(
      ZStream
        .fromFileName("file.zz")
        .orDie
    )
      .via(ZPipeline.utf8Decode)
      .foreach(print(_))
    _ <- print("file.gz: ") *> decompressGzipped(
      ZStream
        .fromFileName("file.gz")
        .orDie
    )
      .via(ZPipeline.utf8Decode)
      .foreach(print(_))
  } yield ()
}
