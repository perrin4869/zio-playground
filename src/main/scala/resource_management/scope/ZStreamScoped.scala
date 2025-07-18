package il.co.dotcore.zio.playground.resource_management.scope

import zio._
import zio.stream._

import java.io.IOException
import scala.io._

object ZStreamScoped extends ZIOAppDefault {

  def acquire(name: => String): ZIO[Any, IOException, Source] =
    ZIO.attemptBlockingIO(Source.fromResource(name))
  // ZIO.attemptBlockingIO(Source.fromFile(name))

  def release(source: => Source): ZIO[Any, Nothing, Unit] =
    ZIO.succeedBlocking(source.close())

  def source(name: => String): ZIO[Scope, IOException, Source] =
    ZIO.acquireRelease(acquire(name))(release(_))

  def lines(name: => String): ZStream[Any, IOException, String] =
    ZStream.scoped(source(name)).flatMap { source =>
      ZStream.fromIteratorSucceed(source.getLines())
    }

  def contents(name: => String): ZIO[Any, IOException, String] =
    lines(name).mkString(before = "", middle = "\n", after = "")

  override def run = contents("cool.txt").debug
}
