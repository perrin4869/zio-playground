package il.co.dotcore.zio.playground.stm

import zio._
import zio.Console._
import zio.stm._

object TReentrantLockExamples extends ZIOAppDefault {

  val reentrantLock = TReentrantLock.make

  val readLockProgram =
    (for {
      lock <- TReentrantLock.make
      _ <- lock.acquireRead
      rst <- lock.readLocked // lock is read-locked once transaction completes
      wst <- lock.writeLocked // lock is not write-locked
    } yield rst && !wst).commit

  val writeLockProgram: UIO[Boolean] =
    (for {
      lock <- TReentrantLock.make
      _ <- lock.acquireWrite
      wst <- lock.writeLocked // lock is write-locked once transaction completes
      rst <- lock.readLocked // lock is not read-locked
    } yield !rst && wst).commit

  val multipleReadLocksProgram: UIO[(Int, Int)] = for {
    lock <- TReentrantLock.make.commit
    fiber0 <- lock.acquireRead.commit.fork // fiber0 acquires a read-lock
    currentState1 <- fiber0.join // 1 read lock held
    fiber1 <- lock.acquireRead.commit.fork // fiber1 acquires a read-lock
    currentState2 <- fiber1.join // 2 read locks held
  } yield (currentState1, currentState2)

  val upgradeDowngradeProgram: UIO[(Boolean, Boolean, Boolean, Boolean)] = for {
    lock <- TReentrantLock.make.commit
    _ <- lock.acquireRead.commit
    _ <- lock.acquireWrite.commit // upgrade
    isWriteLocked <- lock.writeLocked.commit // now write-locked
    isReadLocked <- lock.readLocked.commit // and read-locked
    _ <- lock.releaseWrite.commit // downgrade
    isWriteLockedAfter <- lock.writeLocked.commit // no longer write-locked
    isReadLockedAfter <- lock.readLocked.commit // still read-locked
  } yield (isWriteLocked, isReadLocked, isWriteLockedAfter, isReadLockedAfter)

  val writeLockDemoProgram: UIO[Unit] = for {
    l <- TReentrantLock.make.commit
    _ <- printLine("Beginning test").orDie
    f1 <- (l.acquireRead.commit *> ZIO.sleep(
      5.seconds
    ) *> l.releaseRead.commit).fork
    f2 <- (l.acquireRead.commit *> printLine(
      "read-lock"
    ).orDie *> l.acquireWrite.commit *> printLine(
      "I have upgraded!"
    ).orDie).fork
    _ <- (f1 zip f2).join
  } yield ()

  val saferProgram: UIO[Unit] = for {
    lock <- TReentrantLock.make.commit
    f1 <- ZIO
      .scoped(
        lock.readLock *> ZIO.sleep(5.seconds) *> printLine(
          "Powering down"
        ).orDie
      )
      .fork
    f2 <- ZIO
      .scoped(
        lock.readLock *> lock.writeLock *> printLine(
          "Huzzah, writes are mine"
        ).orDie
      )
      .fork
    _ <- (f1 zip f2).join
  } yield ()

  val program = for {
    _ <- readLockProgram.debug("readLockProgram")
    _ <- writeLockProgram.debug("writeLockProgram")
    _ <- multipleReadLocksProgram.debug("multipleReadLocksProgram")
    _ <- upgradeDowngradeProgram.debug("upgradeDowngradeProgram")
    _ <- writeLockDemoProgram.debug("writeLockDemoProgram")
    _ <- saferProgram.debug("saferProgram")

  } yield ()

  def run = program.debug
}
