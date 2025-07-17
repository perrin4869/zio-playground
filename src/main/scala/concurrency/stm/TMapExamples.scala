package il.co.dotcore.zio.playground.stm

import zio._
import zio.stm._

object TMapExamples extends ZIOAppDefault {

  val emptyTMap: STM[Nothing, TMap[String, Int]] = TMap.empty[String, Int]
  val specifiedValuesTMap: STM[Nothing, TMap[String, Int]] =
    TMap.make(("a", 1), ("b", 2), ("c", 3))
  val iterableTMap: STM[Nothing, TMap[String, Int]] =
    TMap.fromIterable(List(("a", 1), ("b", 2), ("c", 3)))

  val putElem: UIO[TMap[String, Int]] = (for {
    tMap <- TMap.make(("a", 1), ("b", 2))
    _ <- tMap.put("c", 3)
  } yield tMap).commit

  val mergeElem: UIO[TMap[String, Int]] = (for {
    tMap <- TMap.make(("a", 1), ("b", 2), ("c", 3))
    _ <- tMap.merge("c", 4)((x, y) => x * y)
  } yield tMap).commit

  val deleteElem: UIO[TMap[String, Int]] = (for {
    tMap <- TMap.make(("a", 1), ("b", 2), ("c", 3))
    _ <- tMap.delete("b")
  } yield tMap).commit

  val removedEvenValues: UIO[TMap[String, Int]] = (for {
    tMap <- TMap.make(("a", 1), ("b", 2), ("c", 3), ("d", 4))
    _ <- tMap.removeIf((_, v) => v % 2 == 0)
  } yield tMap).commit

  val retainedEvenValues: UIO[TMap[String, Int]] = (for {
    tMap <- TMap.make(("a", 1), ("b", 2), ("c", 3), ("d", 4))
    _ <- tMap.retainIf((_, v) => v % 2 == 0)
  } yield tMap).commit

  val elemGet: UIO[Option[Int]] = (for {
    tMap <- TMap.make(("a", 1), ("b", 2), ("c", 3))
    elem <- tMap.get("c")
  } yield elem).commit

  val elemGetOrElse: UIO[Int] = (for {
    tMap <- TMap.make(("a", 1), ("b", 2), ("c", 3))
    elem <- tMap.getOrElse("d", 4)
  } yield elem).commit

  val transformTMap: UIO[TMap[String, Int]] = (for {
    tMap <- TMap.make(("a", 1), ("b", 2), ("c", 3))
    _ <- tMap.transform((k, v) => k -> v * v)
  } yield tMap).commit

  val shrinkTMap: UIO[TMap[String, Int]] = (for {
    tMap <- TMap.make(("a", 1), ("b", 2), ("c", 3))
    _ <- tMap.transform((_, v) => "d" -> v)
  } yield tMap).commit

  val transformSTMTMap: UIO[TMap[String, Int]] = (for {
    tMap <- TMap.make(("a", 1), ("b", 2), ("c", 3))
    _ <- tMap.transformSTM((k, v) => STM.succeed(k -> v * v))
  } yield tMap).commit

  val transformValuesTMap: UIO[TMap[String, Int]] = (for {
    tMap <- TMap.make(("a", 1), ("b", 2), ("c", 3))
    _ <- tMap.transformValues(v => v * v)
  } yield tMap).commit

  val transformValuesMTMap: UIO[TMap[String, Int]] = (for {
    tMap <- TMap.make(("a", 1), ("b", 2), ("c", 3))
    _ <- tMap.transformValuesSTM(v => STM.succeed(v * v))
  } yield tMap).commit

  val foldTMap: UIO[Int] = (for {
    tMap <- TMap.make(("a", 1), ("b", 2), ("c", 3))
    sum <- tMap.fold(0) { case (acc, (_, v)) => acc + v }
  } yield sum).commit

  val foldSTMTMap: UIO[Int] = (for {
    tMap <- TMap.make(("a", 1), ("b", 2), ("c", 3))
    sum <- tMap.foldSTM(0) { case (acc, (_, v)) => STM.succeed(acc + v) }
  } yield sum).commit

  val foreachTMap = (for {
    tMap <- TMap.make(("a", 1), ("b", 2), ("c", 3))
    tQueue <- TQueue.unbounded[String]
    _ <- tMap.foreach((k, v) => tQueue.offer(s"$k -> $v").unit)
  } yield tMap).commit

  val tMapContainsValue: UIO[Boolean] = (for {
    tMap <- TMap.make(("a", 1), ("b", 2), ("c", 3))
    res <- tMap.contains("a")
  } yield res).commit

  val tMapTuplesList: UIO[List[(String, Int)]] = (for {
    tMap <- TMap.make(("a", 1), ("b", 2), ("c", 3))
    list <- tMap.toList
  } yield list).commit

  val tMapKeysList: UIO[List[String]] = (for {
    tMap <- TMap.make(("a", 1), ("b", 2), ("c", 3))
    list <- tMap.keys
  } yield list).commit

  val tMapValuesList: UIO[List[Int]] = (for {
    tMap <- TMap.make(("a", 1), ("b", 2), ("c", 3))
    list <- tMap.values
  } yield list).commit

  val program = for {
    _ <- putElem.flatMap(_.toMap.commit).debug("putElem")
    _ <- mergeElem.flatMap(_.toMap.commit).debug("mergeElem")
    _ <- deleteElem.flatMap(_.toMap.commit).debug("deleteElem")
    _ <- removedEvenValues.flatMap(_.toMap.commit).debug("removedEvenValues")
    _ <- retainedEvenValues.flatMap(_.toMap.commit).debug("retainedEvenValues")
    _ <- elemGet.debug("elemGet")
    _ <- elemGetOrElse.debug("elemGetOrElse")
    _ <- transformTMap.flatMap(_.toMap.commit).debug("transformTMap")
    _ <- shrinkTMap.flatMap(_.toMap.commit).debug("shrinkTMap")
    _ <- transformSTMTMap.flatMap(_.toMap.commit).debug("transformSTMTMap")
    _ <- transformValuesTMap
      .flatMap(_.toMap.commit)
      .debug("transformValuesTMap")
    _ <- transformValuesMTMap
      .flatMap(_.toMap.commit)
      .debug("transformValuesMTMap")
    _ <- foldTMap.debug("foldTMap")
    _ <- foldSTMTMap.debug("foldSTMTMap")
    _ <- foreachTMap.flatMap(_.toMap.commit).debug("foreachTMap")
    _ <- tMapContainsValue.debug("tMapContainsValue")
    _ <- tMapTuplesList.debug("tMapTuplesList")
    _ <- tMapKeysList.debug("tMapKeysList")
    _ <- tMapValuesList.debug("tMapValuesList")
  } yield ()

  def run = program.debug
}
