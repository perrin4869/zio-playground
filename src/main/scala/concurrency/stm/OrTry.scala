package il.co.dotcore.zio.playground.stm

import zio._
import zio.Console._
import zio.stm._

object OrTry extends ZIOAppDefault {

  def transferMoneyNoMatterWhat(
      from: TRef[Long],
      to: TRef[Long],
      amount: Long
  ): STM[String, Long] =
    for {
      senderBal <- from.get
      _ = println(s"Trying with ${senderBal}")
      _ <- if (senderBal < amount) STM.retry else STM.unit
      _ <- from.update(existing => existing - amount)
      _ <- to.update(existing => existing + amount)
      recvBal <- to.get
    } yield recvBal

  def transferMoneyFailFast(
      from: TRef[Long],
      to: TRef[Long],
      amount: Long
  ): STM[String, Long] =
    transferMoneyNoMatterWhat(from, to, amount) orTry STM.fail(
      "Sender does not have enough of money"
    )

  val program: IO[String, Long] = for {
    sndAcc <- STM.atomically(TRef.make(300L))
    rcvAcc <- STM.atomically(TRef.make(0L))
    recvAmt <- STM.atomically(transferMoneyFailFast(sndAcc, rcvAcc, 500L))
  } yield recvAmt

  def run = program.debug
}
