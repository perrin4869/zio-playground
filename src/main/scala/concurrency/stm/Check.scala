package il.co.dotcore.zio.playground.stm

import zio._
import zio.Console._
import zio.stm._

object Check extends ZIOAppDefault {

  def transferMoneyNoMatterWhat(
      from: TRef[Long],
      to: TRef[Long],
      amount: Long
  ): STM[String, Long] =
    for {
      senderBal <- from.get
      _ = println(s"Trying with ${senderBal}")
      _ <- STM.check(senderBal < amount)
      _ <- from.update(existing => existing - amount)
      _ <- to.update(existing => existing + amount)
      recvBal <- to.get
    } yield recvBal

  val program: IO[String, Long] = for {
    sndAcc <- STM.atomically(TRef.make(300L))
    rcvAcc <- STM.atomically(TRef.make(0L))
    _ <- printLine("Updating in 1 second").orDie
    _ <- STM.atomically(sndAcc.set(1000L)).delay(1.second).fork
    recvAmt <- STM.atomically(transferMoneyNoMatterWhat(sndAcc, rcvAcc, 500L))
  } yield recvAmt

  def run = program.debug
}
