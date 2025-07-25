package il.co.dotcore.zio.playground.streaming.zsink.creating

import zio._
import zio.stream._
import zio.Console._

object Collecting extends ZIOAppDefault {
  object CollectAll {
    val stream: UStream[Int] = ZStream(1, 2, 3, 4, 5)
    val collection: UIO[Chunk[Int]] = stream.run(ZSink.collectAll[Int])
    // Output: Chunk(1, 2, 3, 4, 5)
  }

  object CollectAllToSet {
    val collectAllToSet: ZSink[Any, Nothing, Int, Nothing, Set[Int]] =
      ZSink.collectAllToSet[Int]
    val stream: ZIO[Any, Nothing, Set[Int]] =
      ZStream(1, 3, 2, 3, 1, 5, 1).run(collectAllToSet)
    // Output: Set(1, 3, 2, 5)
  }

  object CollectAllToMap {
    val collectAllToMap: ZSink[Any, Nothing, Int, Nothing, Map[Int, Int]] =
      ZSink.collectAllToMap((_: Int) % 3)(_ + _)
    val stream: ZIO[Any, Nothing, Map[Int, Int]] =
      ZStream(1, 3, 2, 3, 1, 5, 1).run(collectAllToMap)
    // Output: Map(1 -> 3, 0 -> 6, 2 -> 7)
  }

  object CollectAllN {
    val stream = ZStream(1, 2, 3, 4, 5).run(
      ZSink.collectAllN(3)
    )
    // Output: Chunk(1,2,3), Chunk(4,5)
  }

  object CollectAllWhile {
    val stream = ZStream(1, 2, 0, 4, 0, 6, 7).run(
      ZSink.collectAllWhile(_ != 0)
    )
    // Output: Chunk(1,2), Chunk(4), Chunk(6,7)
  }

  object CollectAllToMapN {
    val stream = ZStream(1, 2, 0, 4, 5).run(
      ZSink.collectAllToMapN[Nothing, Int, Int](10)(_ % 3)(_ + _)
    )
    // Output: Map(1 -> 5, 2 -> 7, 0 -> 0)
  }

  object CollectAllToSetN {
    val stream = ZStream(1, 2, 1, 2, 1, 3, 0, 5, 0, 2).run(
      ZSink.collectAllToSetN(3)
    )
    // Output: Set(1,2,3), Set(0,5,2), Set(1)
  }

  override def run = for {
    _ <- CollectAll.collection.debug("CollectAll.collection")
    _ <- CollectAllToSet.stream.debug("CollectAllToSet.stream")
    _ <- CollectAllToMap.stream.debug("CollectAllToMap.stream")
    _ <- CollectAllN.stream.debug("CollectAllN.stream")
    _ <- print("CollectAllN transduce: ") *> ZStream(1, 2, 3, 4, 5)
      .transduce(
        ZSink.collectAllN[Int](3)
      )
      .run(ZSink.foreachChunk(printLine(_)))
    _ <- CollectAllWhile.stream.debug("CollectAllWhile.stream")
    _ <- CollectAllToMapN.stream.debug("CollectAllToMapN.stream")
    _ <- CollectAllToSetN.stream.debug("CollectAllToSetN.stream")
  } yield ()
}
