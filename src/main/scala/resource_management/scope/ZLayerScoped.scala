package il.co.dotcore.zio.playground.resource_management.scope

import zio._
import zio.stream._

import java.io.IOException
import scala.io._

object ZLayerScoped extends ZIOAppDefault {

  def acquire(name: => String): ZIO[Any, IOException, Source] =
    ZIO.attemptBlockingIO(Source.fromResource(name))
  // ZIO.attemptBlockingIO(Source.fromFile(name))

  def release(source: => Source): ZIO[Any, Nothing, Unit] =
    ZIO.succeedBlocking(source.close())

  def source(name: => String): ZIO[Scope, IOException, Source] =
    ZIO.acquireRelease(acquire(name))(release(_))

  def sourceLayer(name: => String): ZLayer[Any, IOException, Source] =
    ZLayer.scoped(source(name))

  def contents: ZIO[Source, IOException, Chunk[String]] =
    ZIO.service[Source].flatMap { source =>
      ZIO.attemptBlockingIO(Chunk.fromIterator(source.getLines()))
    }

  override def run = contents.provideLayer(sourceLayer("cool.txt")).debug
}
