package il.co.dotcore.zio.playground.streaming.zsink.creating

import zio._
import zio.stream._
import zio.Console._

object Folding extends ZIOAppDefault {
  object Fold {
    val stream = ZStream
      .iterate(0)(_ + 1)
      .run(
        ZSink.fold(0)(sum => sum <= 10)((acc, n: Int) => acc + n)
      )
    // Output: 15
  }

  object FoldWeighted {
    val stream = ZStream(3, 2, 4, 1, 5, 6, 2, 1, 3, 5, 6)
      .transduce(
        ZSink
          .foldWeighted(Chunk[Int]())(
            (_, _: Int) => 1,
            3
          ) { (acc, el) =>
            acc ++ Chunk(el)
          }
      )
    // Output: Chunk(3,2,4),Chunk(1,5,6),Chunk(2,1,3),Chunk(5,6)

    val stream2 = ZStream(1, 2, 2, 4, 2, 1, 1, 1, 0, 2, 1, 2)
      .transduce(
        ZSink
          .foldWeighted(Chunk[Int]())(
            (_, i: Int) => i.toLong,
            5
          ) { (acc, el) =>
            acc ++ Chunk(el)
          }
      )
    // Output: Chunk(1,2,2),Chunk(4),Chunk(2,1,1,1,0),Chunk(2,1,2)
  }

  object FoldDecompose {
    val stream = ZStream(1, 2, 2, 2, 1, 6, 1, 7, 2, 1, 2)
      .transduce(
        ZSink
          .foldWeightedDecompose(Chunk[Int]())(
            (_, i: Int) => i.toLong,
            5,
            (i: Int) => if (i > 5) Chunk(i - 1, 1) else Chunk(i)
          )((acc, el) => acc ++ Chunk.succeed(el))
      )
    // Ouput: Chunk(1,2,2),Chunk(2,1),Chunk(5),Chunk(1,1),Chunk(5),Chunk(1,1,2,1),Chunk(2)
  }

  object FoldUntil {
    val stream = ZStream(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
      .run(ZSink.foldUntil(0, 3)(_ + _))
    // Output: 6, 15, 24, 10
  }

  object FoldLeft {
    val stream: ZIO[Any, Nothing, Int] =
      ZStream(1, 2, 3, 4).run(ZSink.foldLeft[Int, Int](0)(_ + _))
    // Output: 10
  }

  override def run = for {
    _ <- Fold.stream.debug("Fold.stream")
    _ <- print("FoldWeighted.stream: ") *> FoldWeighted.stream.runForeachChunk(
      printLine(_)
    )
    _ <- print("FoldWeighted.stream2: ") *> FoldWeighted.stream2
      .runForeachChunk(printLine(_))
    _ <- print("FoldDecompose.stream2: ") *> FoldDecompose.stream
      .runForeachChunk(printLine(_))
    _ <- FoldUntil.stream.debug("FoldUntil.stream")
    _ <- FoldLeft.stream.debug("FoldLeft.stream")
  } yield ()
}
