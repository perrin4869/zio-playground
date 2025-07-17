package il.co.dotcore.zio.playground.stm

import zio._
import zio.stm._

object TArrayExamples extends ZIOAppDefault {

  val emptyTArray: STM[Nothing, TArray[Int]] = TArray.empty[Int]
  val specifiedValuesTArray: STM[Nothing, TArray[Int]] = TArray.make(1, 2, 3)
  val iterableTArray: STM[Nothing, TArray[Int]] =
    TArray.fromIterable(List(1, 2, 3))

  val tArrayGetElem: UIO[Int] = (for {
    tArray <- TArray.make(1, 2, 3, 4)
    elem <- tArray(2)
  } yield elem).commit

  val tArrayUpdateElem: UIO[TArray[Int]] = (for {
    tArray <- TArray.make(1, 2, 3, 4)
    _ <- tArray.update(2, el => el + 10)
  } yield tArray).commit

  val tArrayUpdateMElem: UIO[TArray[Int]] = (for {
    tArray <- TArray.make(1, 2, 3, 4)
    _ <- tArray.updateSTM(2, el => STM.succeed(el + 10))
  } yield tArray).commit

  val transformTArray: UIO[TArray[Int]] = (for {
    tArray <- TArray.make(1, 2, 3, 4)
    _ <- tArray.transform(a => a * a)
  } yield tArray).commit

  val transformSTMTArray: UIO[TArray[Int]] = (for {
    tArray <- TArray.make(1, 2, 3, 4)
    _ <- tArray.transformSTM(a => STM.succeed(a * a))
  } yield tArray).commit

  val foldTArray: UIO[Int] = (for {
    tArray <- TArray.make(1, 2, 3, 4)
    sum <- tArray.fold(0)(_ + _)
  } yield sum).commit

  val foldSTMTArray: UIO[Int] = (for {
    tArray <- TArray.make(1, 2, 3, 4)
    sum <- tArray.foldSTM(0)((acc, el) => STM.succeed(acc + el))
  } yield sum).commit

  val foreachTArray = (for {
    tArray <- TArray.make(1, 2, 3, 4)
    tQueue <- TQueue.unbounded[Int]
    _ <- tArray.foreach(a => tQueue.offer(a).unit)
  } yield tArray).commit

  val program = for {
    _ <- tArrayGetElem.debug("tArrayGetElem")
    _ <- tArrayUpdateElem.flatMap(_.toList.commit).debug("tArrayUpdateElem")
    _ <- tArrayUpdateMElem.flatMap(_.toList.commit).debug("tArrayUpdateMElem")
    _ <- transformTArray.flatMap(_.toList.commit).debug("transformTArray")
    _ <- transformSTMTArray.flatMap(_.toList.commit).debug("transformSTMTArray")
    _ <- foldTArray.debug("foldTArray")
    _ <- foldSTMTArray.debug("foldSTMTArray")
    _ <- foreachTArray.flatMap(_.toList.commit).debug("foreachTArray")
  } yield ()

  def run = program.debug
}
