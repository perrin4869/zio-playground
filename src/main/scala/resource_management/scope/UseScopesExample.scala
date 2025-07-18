package il.co.dotcore.zio.playground.resource_management.scope

import zio._

import java.io.IOException
import scala.io._

object UseScopesExample extends ZIOAppDefault {

  def acquire(name: => String): ZIO[Any, IOException, Source] =
    ZIO.attemptBlockingIO(Source.fromResource(name))
  // ZIO.attemptBlockingIO(Source.fromFile(name))

  def release(source: => Source): ZIO[Any, Nothing, Unit] =
    ZIO.succeedBlocking(source.close())

  def source(name: => String): ZIO[Scope, IOException, Source] =
    ZIO.acquireRelease(acquire(name))(release(_))

  def contents(name: => String): ZIO[Any, IOException, Chunk[String]] =
    for {
      scope <- Scope.make
      lines <- scope.use(
        for {
          source <- source(name)
          lines <- ZIO.attemptBlockingIO(Chunk.fromIterator(source.getLines()))
        } yield lines
      )
    } yield lines

  override def run = contents("cool.txt").debug
}
