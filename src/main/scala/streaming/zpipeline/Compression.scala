package il.co.dotcore.zio.playground.streaming.zpipeline

import zio._
import zio.Console._
import zio.stream._

object Compression extends ZIOAppDefault {
  import zio.stream.ZPipeline.deflate
  import zio.stream.compression.{
    CompressionLevel,
    CompressionStrategy,
    FlushMode
  }

  def compressWithDeflate(
      clearText: ZStream[Any, Nothing, Byte]
  ): ZStream[Any, Nothing, Byte] = {
    val bufferSize: Int =
      64 * 1024 // Internal buffer size. Few times bigger than upstream chunks should work well.
    val noWrap: Boolean = false // For HTTP Content-Encoding should be false.
    val level: CompressionLevel = CompressionLevel.DefaultCompression
    val strategy: CompressionStrategy = CompressionStrategy.DefaultStrategy
    val flushMode: FlushMode = FlushMode.NoFlush
    clearText.via(deflate(bufferSize, noWrap, level, strategy, flushMode))
  }

  def deflateWithDefaultParameters(
      clearText: ZStream[Any, Nothing, Byte]
  ): ZStream[Any, Nothing, Byte] =
    clearText.via(deflate())

  override def run = for {
    _ <- deflateWithDefaultParameters(
      ZStream
        .fromFileName("file.txt")
        .orDie
    )
      .run(
        // confirm on linux with `zlib-flate -uncompress < input.deflate`
        ZSink.fromFileName("file.zz")
      )
    _ <- ZStream
      .fromFileName("file.txt")
      .via(
        ZPipeline.gzip(
          bufferSize = 64 * 1024,
          level = CompressionLevel.DefaultCompression,
          strategy = CompressionStrategy.DefaultStrategy,
          flushMode = FlushMode.NoFlush
        )
      )
      .run(
        ZSink.fromFileName("file.gz")
      )
  } yield ()
}
