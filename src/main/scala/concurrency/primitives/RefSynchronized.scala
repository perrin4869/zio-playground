package il.co.dotcore.myziotest.concurrency.primitives

import zio._
import zio.Console._

object RefSynchronized extends ZIOAppDefault {

  val users = 1 to 10

  val meanAge =
    for {
      ref <- Ref.Synchronized.make(0)
      _ <- ZIO.foreachPar(users) { user =>
        ref.updateZIO(sumOfAges =>
          printLine(
            s"Fetching age of user $user, current sum is $sumOfAges"
          ) *> ZIO.sleep(
            1000 millis
          ) *> Random
            .nextIntBetween(0, 100)
            .map(_ + sumOfAges) <* printLine(s"Updated user $user")
        )
      }
      v <- ref.get
    } yield (v / users.length)

  override def run = meanAge.debug
}
