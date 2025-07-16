package il.co.dotcore.myziotest

import zio._
import zio.concurrent._

// Unsafe State Management
object UnsafeCountRequests extends ZIOAppDefault {

  def request(counter: Ref[Int]) = for {
    current <- counter.get
    _ <- counter.set(current + 1)
  } yield ()

  private val initial = 0
  private val myApp =
    for {
      ref <- Ref.make(initial)
      _ <- request(ref) zipPar request(ref)
      rn <- ref.get
      _ <- Console.printLine(s"total requests performed: $rn")
    } yield ()

  def run = myApp
}
