package il.co.dotcore.zio.playground.streaming.zstream

import zio._
import zio.stream._
import zio.Console._

import java.io.BufferedReader
import java.nio.file.Files
import java.io.IOException
import scala.io.Source

object CreatingZIOStreams extends ZIOAppDefault {

  val stream: ZStream[Any, Nothing, Int] = ZStream(1, 2, 3)
  val unit: ZStream[Any, Nothing, Unit] = ZStream.unit
  val never: ZStream[Any, Nothing, Nothing] = ZStream.never
  val repeat: ZStream[Any, Nothing, Int] = ZStream(1).repeat(Schedule.forever)
  val nats: ZStream[Any, Nothing, Int] = ZStream.iterate(1)(_ + 1)
  val range: ZStream[Any, Nothing, Int] = ZStream.range(1, 5)

  trait Foo

  val fooStream: ZStream[Foo, Nothing, Foo] = ZStream.service[Foo]

  val chunk = ZStream.fromChunk(Chunk(1, 2, 3))
  val chunks = ZStream.fromChunks(Chunk(1, 2, 3), Chunk(4, 5, 6))

  val scopedStream: ZStream[Any, Throwable, BufferedReader] =
    ZStream.scoped(
      ZIO.fromAutoCloseable(
        ZIO.attemptBlocking(
          Files.newBufferedReader(java.nio.file.Paths.get("file.txt"))
        )
      )
    )

  val s1: ZStream[Any, String, Nothing] = ZStream.fail("Uh oh!")
  val s2: ZStream[Any, Nothing, Int] = ZStream.succeed(5)

  val readline: ZStream[Any, IOException, String] =
    ZStream.fromZIO(Console.readLine)
  val randomInt: ZStream[Any, Nothing, Int] = ZStream.fromZIO(Random.nextInt)

  val userInput: ZStream[Any, IOException, String] =
    ZStream.fromZIOOption(
      Console.readLine.mapError(Option(_)).flatMap {
        case "EOF" => ZIO.fail[Option[IOException]](None)
        case o     => ZIO.succeed(o)
      }
    )

  // Asynchronous Callback-based API
  def registerCallback(
      name: String,
      onEvent: Int => Unit,
      onError: Throwable => Unit
  ): Unit = onEvent(42)

  // Lifting an Asynchronous API to ZStream
  val async = ZStream.async[Any, Throwable, Int] { cb =>
    registerCallback(
      "foo",
      // event => cb(ZIO.succeed(Chunk(event))),
      event => {
        cb(ZIO.succeed(Chunk(event)))
        cb(ZIO.succeed(Chunk(event)))
      },
      error => cb(ZIO.fail(error).mapError(Some(_)))
    )
  }

  // Lifting an Asynchronous API to ZStream
  val asyncFinite = ZStream.async[Any, Throwable, Int] { cb =>
    registerCallback(
      "foo",
      // event => cb(ZIO.succeed(Chunk(event))),
      event => {
        cb(ZIO.succeed(Chunk(event)))
        cb(ZIO.fail(None))
      },
      error => cb(ZIO.fail(error).mapError(Some(_)))
    )
  }

  val fromIterator1: ZStream[Any, Throwable, Int] =
    ZStream.fromIterator(Iterator(1, 2, 3))
  val fromIterator2: ZStream[Any, Throwable, Int] =
    ZStream.fromIterator(Iterator.range(1, 4))
  val fromIterator3: ZStream[Any, Throwable, Int] =
    ZStream.fromIterator(Iterator.continually(0))

  val lines: ZStream[Any, Throwable, String] =
    ZStream.fromIteratorZIO(
      ZIO.attempt(Source.fromResource("cool.txt").getLines())
    )
  // ZStream.fromIteratorZIO(ZIO.attempt(Source.fromFile("file.txt").getLines()))

  val linesScoped: ZStream[Any, Throwable, String] =
    ZStream.fromIteratorScoped(
      ZIO
        .fromAutoCloseable(
          // ZIO.attempt(scala.io.Source.fromFile("file.txt"))
          ZIO.attempt(scala.io.Source.fromResource("cool.txt"))
        )
        .map(_.getLines())
    )

  val list = ZStream.fromIterable(List(1, 2, 3))

  case class User(name: String)
  trait Database {
    def getUsers: Task[List[User]]
  }

  object Database {
    def getUsers: ZIO[Database, Throwable, List[User]] =
      ZIO.serviceWithZIO[Database](_.getUsers)
  }

  val users: ZStream[Database, Throwable, User] =
    ZStream.fromIterableZIO(Database.getUsers)

  val repeatZero: ZStream[Any, Nothing, Int] = ZStream.repeat(0)
  val repeatZeroEverySecond: ZStream[Any, Nothing, Int] =
    ZStream.repeatWithSchedule(0, Schedule.spaced(1.seconds))
  val randomInts: ZStream[Any, Nothing, Int] =
    ZStream.repeatZIO(Random.nextInt)

  val userInputs: ZStream[Any, IOException, String] =
    ZStream.repeatZIOOption(
      Console.readLine.mapError(Option(_)).flatMap {
        case "EOF" => ZIO.fail[Option[IOException]](None)
        case o     => ZIO.succeed(o)
      }
    )

  def drainIterator[A](it: Iterator[A]): ZStream[Any, Throwable, A] =
    ZStream.repeatZIOOption {
      ZIO.attempt(it.hasNext).mapError(Some(_)).flatMap { hasNext =>
        if (hasNext) ZIO.attempt(it.next()).mapError(Some(_))
        else ZIO.fail(None)
      }
    }

  val tick: ZStream[Any, Nothing, Unit] = ZStream.tick(1.seconds)

  val unfold: ZStream[Any, Nothing, Int] =
    ZStream.unfold(1)(n => Some((n, n + 1)))

  def countdown(n: Int) = ZStream.unfold(n) {
    case 0 => None
    case s => Some((s, s - 1))
  }

  val inputs: ZStream[Any, IOException, String] = ZStream.unfoldZIO(()) { _ =>
    Console.readLine.map {
      case "exit" => None
      case i      => Some((i, ()))
    }
  }

  val paginate = ZStream.paginate(0) { s =>
    s -> (if (s < 3) Some(s + 1) else None)
  }

  object WrappedStreams {
    val wrappedWithZIO: UIO[ZStream[Any, Nothing, Int]] =
      ZIO.succeed(ZStream(1, 2, 3))
    val s1: ZStream[Any, Nothing, Int] =
      ZStream.unwrap(wrappedWithZIO)

    val wrappedWithZIOScoped = ZIO.succeed(ZStream(1, 2, 3))
    val s2: ZStream[Any, Nothing, Int] =
      ZStream.unwrapScoped(wrappedWithZIOScoped)
  }

  object JavaIO {
    import java.nio.file.Paths
    val file: ZStream[Any, Throwable, Byte] =
      ZStream.fromPath(Paths.get("file.txt"))

    import java.io.FileInputStream
    val fromInputStream: ZStream[Any, IOException, Byte] =
      ZStream.fromInputStream(new FileInputStream("file.txt"))

    val fromInputStreamZIO: ZStream[Any, IOException, Byte] =
      ZStream.fromInputStreamZIO(
        ZIO
          .attempt(new FileInputStream("file.txt"))
          .refineToOrDie[IOException]
      )

    val scoped: ZIO[Scope, IOException, FileInputStream] =
      ZIO
        .fromAutoCloseable(
          ZIO.attempt(new FileInputStream("file.txt"))
        )
        .refineToOrDie[IOException]
    val fromInputStreamScoped: ZStream[Any, IOException, Byte] =
      ZStream.fromInputStreamScoped(scoped)

    val fromResource: ZStream[Any, IOException, Byte] =
      ZStream.fromResource("cool.txt")

    import java.io.FileReader
    val fromReader: ZStream[Any, IOException, Char] =
      ZStream.fromReader(new FileReader("file.txt"))
  }

  object JavaStream {
    val stream: ZStream[Any, Throwable, Int] =
      ZStream.fromJavaStream(java.util.stream.Stream.of(1, 2, 3))
  }

  object QueueAndHub {
    val fromChunkHubScoped = for {
      promise <- Promise.make[Nothing, Unit]
      hub <- Hub.unbounded[Chunk[Int]]
      scoped = ZStream.fromChunkHubScoped(hub).tap(_ => promise.succeed(()))
      stream = ZStream.unwrapScoped(scoped)
      fiber <- stream.foreach(printLine(_)).fork
      _ <- promise.await
      _ <- hub.publish(Chunk(1, 2, 3))
      _ <- fiber.join
    } yield ()

    import zio.stm._
    val fromTQueue = for {
      q <- STM.atomically(TQueue.unbounded[Int])
      stream = ZStream.fromTQueue(q)
      fiber <- stream.foreach(printLine(_)).fork
      _ <- STM.atomically(q.offer(1))
      _ <- STM.atomically(q.offer(2))
      _ <- fiber.join
    } yield ()
  }

  object FromSchedule {
    val stream: ZStream[Any, Nothing, Long] =
      ZStream.fromSchedule(Schedule.spaced(1.second) >>> Schedule.recurs(10))
  }

  override def run = for {
    _ <- stream.toIterator.map(_.toList).debug("stream")
    _ <- unit.toIterator.map(_.toList).debug("unit")
    // this one hangs:
    // _ <- never.toIterator.map(_.toList).debug("never")
    // this one runs forever:
    // _ <- repeat.runForeachChunk(printLine(_))
    _ <- print("repeat: ") *> repeat
      .take(10)
      .runForeachChunk(print(_) *> print(" ")) *> print("\n")
    _ <- print("nats: ") *> nats
      .take(10)
      .runForeachChunk(print(_) *> print(" ")) *> print("\n")
    _ <- range.toIterator.map(_.toList).debug("range")
    _ <- fooStream
      .provideLayer(ZLayer.succeed(new Foo {
        override def toString(): String = "MyFoo"
      }))
      .toIterator
      .map(_.toList)
      .debug("fooStream")
    _ <- scopedStream.toIterator.map(_.toList).debug("scopedStream")
    _ <- s1.toIterator.map(_.toList).debug("s1")
    _ <- s2.toIterator.map(_.toList).debug("s2")
    _ <- chunk.toIterator.map(_.toList).debug("chunk")
    _ <- chunks.toIterator.map(_.toList).debug("chunks")
    // reads from input:
    // _ <- readline.toIterator.map(_.toList).debug("readline")
    _ <- randomInt.toIterator.map(_.toList).debug("randomInt")
    // reads from input:
    // _ <- userInput.toIterator.map(_.toList).debug("userInput")
    _ <- print("async: ") *> async
      .take(2)
      .runForeachChunk(print(_) *> print(" ")) *> print("\n")
    _ <- asyncFinite.toIterator.map(_.toList).debug("asyncFinite")
    _ <- fromIterator1.toIterator.map(_.toList).debug("fromIterator1")
    _ <- fromIterator2.toIterator.map(_.toList).debug("fromIterator2")
    _ <- print("fromIterator3: ") *> fromIterator3
      .take(5)
      .runForeachChunk(print(_) *> print(" ")) *> print("\n")
    _ <- lines.toIterator.map(_.toList).debug("lines")
    _ <- linesScoped.toIterator.map(_.toList).debug("linesScoped")
    _ <- list.toIterator.map(_.toList).debug("list")
    _ <- users
      .provideLayer(ZLayer.succeed(new Database {
        override def getUsers =
          ZIO.succeed(List(User("myuser"), User("myotheruser")))
      }))
      .toIterator
      .map(_.toList)
      .debug("users")
    _ <- print("repeatZero: ") *> repeatZero
      .take(5)
      .runForeachChunk(print(_) *> print(" ")) *> print("\n")
    _ <- print("repeatZeroEverySecond: ") *> repeatZeroEverySecond
      .take(5)
      .runForeachChunk(print(_) *> print(" ")) *> print("\n")
    _ <- print("randomInts: ") *> randomInts
      .take(5)
      .runForeachChunk(print(_) *> print(" ")) *> print("\n")
    // reads from input:
    // _ <- print("userInputs: ") *> userInputs
    //   .runForeachChunk(print(_) *> print(" ")) *> print("\n")
    _ <- drainIterator(List(1, 2, 3, 4).iterator).toIterator
      .map(_.toList)
      .debug("drainIterator")
    _ <- print("tick: ") *> tick
      .take(5)
      .runForeachChunk(print(_) *> print(" ")) *> print("\n")
    _ <- print("unfold: ") *> unfold
      .take(5)
      .runForeachChunk(print(_) *> print(" ")) *> print("\n")
    _ <- print("countdown: ") *> countdown(10)
      .runForeachChunk(print(_) *> print(" ")) *> print("\n")
    // reads from input:
    // _ <- print("inputs: ") *> inputs
    //   .runForeachChunk(print(_) *> print(" ")) *> print("\n")
    _ <- paginate.toIterator
      .map(_.toList)
      .debug("paginate")
    _ <- WrappedStreams.s1.toIterator
      .map(_.toList)
      .debug("WrappedStreams.s1")
    _ <- WrappedStreams.s2.toIterator
      .map(_.toList)
      .debug("WrappedStreams.s2")
    _ <- JavaIO.file.toIterator
      .map(_.toList)
      .debug("JavaIO.file")
    _ <- JavaIO.fromInputStream.toIterator
      .map(_.toList)
      .debug("JavaIO.fromInputStream")
    _ <- JavaIO.fromInputStreamZIO.toIterator
      .map(_.toList)
      .debug("JavaIO.fromInputStreamZIO")
    _ <- JavaIO.fromInputStreamScoped.toIterator
      .map(_.toList)
      .debug("JavaIO.fromInputStreamScoped")
    _ <- JavaIO.fromResource.toIterator
      .map(_.toList)
      .debug("JavaIO.fromResource")
    _ <- JavaIO.fromReader.toIterator
      .map(_.toList)
      .debug("JavaIO.fromReader")
    _ <- JavaStream.stream.toIterator
      .map(_.toList)
      .debug("JavaStream.stream")
    // does not close
    // _ <- QueueAndHub.fromChunkHubScoped.debug("QueueAndHub.fromChunkHubScoped")
    // does not close
    // _ <- QueueAndHub.fromTQueue.debug("QueueAndHub.fromTQueue")
    _ <- print("fromSchedule: ") *> FromSchedule.stream
      .runForeachChunk(print(_) *> print(" ")) *> print("\n")
  } yield ()
}
