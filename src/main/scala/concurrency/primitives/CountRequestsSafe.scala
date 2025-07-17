package il.co.dotcore.zio.playground.concurrency.primitives

import zio._
import zio.concurrent._

// Safe State Management
object CountRequestsSafe extends ZIOAppDefault {

  // Safe in Concurrent Environment
  def request(counter: Ref[Int]) = {
    for {
      rn <- counter.modify(c => (c + 1, c + 1))
      _ <- Console.printLine(s"request number received: $rn")
    } yield ()
  }

  private val initial = 0
  private val myApp =
    for {
      ref <- Ref.make(initial)
      _ <- request(ref) zipPar request(ref)
      rn <- ref.get
      _ <- Console.printLine(s"total requests performed: $rn").orDie
    } yield ()

  def run = myApp
}
