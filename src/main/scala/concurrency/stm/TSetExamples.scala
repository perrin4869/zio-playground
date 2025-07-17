package il.co.dotcore.zio.playground.stm

import zio._
import zio.stm._

object TSetExamples extends ZIOAppDefault {

  val emptyTSet: STM[Nothing, TSet[Int]] = TSet.empty[Int]
  val specifiedValuesTSet: STM[Nothing, TSet[Int]] = TSet.make(1, 2, 3)
  val iterableTSet: STM[Nothing, TSet[Int]] = TSet.fromIterable(List(1, 2, 3))

  val putElem: UIO[TSet[Int]] = (for {
    tSet <- TSet.make(1, 2)
    _ <- tSet.put(3)
  } yield tSet).commit

  val deleteElem: UIO[TSet[Int]] = (for {
    tSet <- TSet.make(1, 2, 3)
    _ <- tSet.delete(1)
  } yield tSet).commit

  val removedEvenElems: UIO[TSet[Int]] = (for {
    tSet <- TSet.make(1, 2, 3, 4)
    _ <- tSet.removeIf(_ % 2 == 0)
  } yield tSet).commit

  val retainedEvenElems: UIO[TSet[Int]] = (for {
    tSet <- TSet.make(1, 2, 3, 4)
    _ <- tSet.retainIf(_ % 2 == 0)
  } yield tSet).commit

  // unionTSet = {1, 2, 3, 4, 5, 6}
  val unionTSet: UIO[TSet[Int]] = (for {
    tSetA <- TSet.make(1, 2, 3, 4)
    tSetB <- TSet.make(3, 4, 5, 6)
    _ <- tSetA.union(tSetB)
  } yield tSetA).commit

  // intersectionTSet = {3, 4}
  val intersectionTSet: UIO[TSet[Int]] = (for {
    tSetA <- TSet.make(1, 2, 3, 4)
    tSetB <- TSet.make(3, 4, 5, 6)
    _ <- tSetA.intersect(tSetB)
  } yield tSetA).commit

  val transformTSet: UIO[TSet[Int]] = (for {
    tSet <- TSet.make(1, 2, 3, 4)
    _ <- tSet.transform(a => a * a)
  } yield tSet).commit

  val shrinkTSet: UIO[TSet[Int]] = (for {
    tSet <- TSet.make(1, 2, 3, 4)
    _ <- tSet.transform(_ => 1)
  } yield tSet).commit

  val transformSTMTSet: UIO[TSet[Int]] = (for {
    tSet <- TSet.make(1, 2, 3, 4)
    _ <- tSet.transformSTM(a => STM.succeed(a * a))
  } yield tSet).commit

  val foldTSet: UIO[Int] = (for {
    tSet <- TSet.make(1, 2, 3, 4)
    sum <- tSet.fold(0)(_ + _)
  } yield sum).commit

  val foldSTMTSet: UIO[Int] = (for {
    tSet <- TSet.make(1, 2, 3, 4)
    sum <- tSet.foldSTM(0)((acc, el) => STM.succeed(acc + el))
  } yield sum).commit

  val foreachTSet = (for {
    tSet <- TSet.make(1, 2, 3, 4)
    tQueue <- TQueue.unbounded[Int]
    _ <- tSet.foreach(a => tQueue.offer(a).unit)
  } yield tSet).commit

  val tSetContainsElem: UIO[Boolean] = (for {
    tSet <- TSet.make(1, 2, 3, 4)
    res <- tSet.contains(3)
  } yield res).commit

  val tSetToList: UIO[List[Int]] = (for {
    tSet <- TSet.make(1, 2, 3, 4)
    list <- tSet.toList
  } yield list).commit

  val tSetSize: UIO[Int] = (for {
    tSet <- TSet.make(1, 2, 3, 4)
    size <- tSet.size
  } yield size).commit

  val program = for {
    _ <- putElem.flatMap(_.toSet.commit).debug("putElem")
    _ <- deleteElem.flatMap(_.toSet.commit).debug("deleteElem")
    _ <- removedEvenElems.flatMap(_.toSet.commit).debug("removedEvenElems")
    _ <- retainedEvenElems.flatMap(_.toSet.commit).debug("retainedEvenElems")
    _ <- unionTSet.flatMap(_.toSet.commit).debug("unionTSet")
    _ <- intersectionTSet.flatMap(_.toSet.commit).debug("intersectionTSet")
    _ <- transformTSet.flatMap(_.toSet.commit).debug("transformTSet")
    _ <- shrinkTSet.flatMap(_.toSet.commit).debug("shrinkTSet")
    _ <- transformSTMTSet.flatMap(_.toSet.commit).debug("transformSTMTSet")
    _ <- foldTSet.debug("foldTSet")
    _ <- foldSTMTSet.debug("foldSTMTSet")
    _ <- foreachTSet.flatMap(_.toSet.commit).debug("foreachTSet")
    _ <- tSetContainsElem.debug("tSetContainsElem")
    _ <- tSetToList.debug("tSetToList")
    _ <- tSetSize.debug("tSetSize")
  } yield ()

  def run = program.debug
}
