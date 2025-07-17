package il.co.dotcore.zio.playground.core.zio

import zio._

import java.io.IOException

object Tap extends ZIOAppDefault {
  def isPrime(n: Int): Boolean =
    if (n <= 1) false else (2 until n).forall(i => n % i != 0)

  val myApp: ZIO[Any, IOException, Unit] =
    for {
      ref <- Ref.make(List.empty[Int])
      prime <-
        Random
          .nextIntBetween(0, Int.MaxValue)
          .tap(random => ref.update(_ :+ random))
          .repeatUntil(isPrime)
      _ <- Console.printLine(s"found a prime number: $prime")
      tested <- ref.get
      _ <- Console.printLine(
        s"list of tested numbers: ${tested.mkString(", ")}"
      )
    } yield ()

  def run = myApp
}
