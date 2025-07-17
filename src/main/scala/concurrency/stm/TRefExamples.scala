package il.co.dotcore.zio.playground.stm

import zio._
import zio.stm._

object TRefExamples extends ZIOAppDefault {

  val createTRef: STM[Nothing, TRef[Int]] = TRef.make(10)
  val commitTRef: UIO[TRef[Int]] = TRef.makeCommit(10)

  val retrieveSingle: UIO[Int] = (for {
    tRef <- TRef.make(10)
    value <- tRef.get
  } yield value).commit

  val retrieveMultiple: UIO[Int] = for {
    tRef <- TRef.makeCommit(10)
    value <- tRef.get.commit
  } yield value

  val setSingle: UIO[Int] = (for {
    tRef <- TRef.make(10)
    _ <- tRef.set(20)
    nValue <- tRef.get
  } yield nValue).commit

  val setMultiple: UIO[Int] = for {
    tRef <- TRef.makeCommit(10)
    nValue <- tRef.set(20).flatMap(_ => tRef.get).commit
  } yield nValue

  val updateSingle: UIO[Int] = (for {
    tRef <- TRef.make(10)
    nValue <- tRef.updateAndGet(_ + 20)
  } yield nValue).commit

  val updateMultiple: UIO[Int] = for {
    tRef <- TRef.makeCommit(10)
    nValue <- tRef.updateAndGet(_ + 20).commit
  } yield nValue

  val modifySingle: UIO[(String, Int)] = (for {
    tRef <- TRef.make(10)
    mValue <- tRef.modify(v => ("Zee-Oh", v + 10))
    nValue <- tRef.get
  } yield (mValue, nValue)).commit

  val modifyMultiple: UIO[(String, Int)] = for {
    tRef <- TRef.makeCommit(10)
    tuple2 <- tRef.modify(v => ("Zee-Oh", v + 10)).zip(tRef.get).commit
  } yield tuple2

  def transfer(
      tSender: TRef[Int],
      tReceiver: TRef[Int],
      amount: Int
  ): UIO[Int] = {
    STM.atomically {
      for {
        _ <- tSender.get.retryUntil(_ >= amount)
        _ <- tSender.update(_ - amount)
        nAmount <- tReceiver.updateAndGet(_ + amount)
      } yield nAmount
    }
  }

  val transferredMoney: UIO[String] = for {
    tSender <- TRef.makeCommit(50)
    tReceiver <- TRef.makeCommit(100)
    _ <- transfer(tSender, tReceiver, 50).fork
    _ <- tSender.get.retryUntil(_ == 0).commit
    tuple2 <- tSender.get.zip(tReceiver.get).commit
    (senderBalance, receiverBalance) = tuple2
  } yield s"sender: $senderBalance & receiver: $receiverBalance"

  val program = for {
    _ <- retrieveSingle.debug("retrieveSingle")
    _ <- retrieveMultiple.debug("retrieveMultiple")
    _ <- setSingle.debug("setSingle")
    _ <- setMultiple.debug("setMultiple")
    _ <- updateSingle.debug("updateSingle")
    _ <- updateMultiple.debug("updateMultiple")
    _ <- modifySingle.debug("modifySingle")
    _ <- modifyMultiple.debug("modifyMultiple")
    _ <- transferredMoney.debug("transferredMoney")
  } yield ()

  def run = program.debug
}
