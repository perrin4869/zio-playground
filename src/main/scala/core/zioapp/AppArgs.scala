package il.co.dotcore.myziotest

import zio._
import zio.Console._

object AppArgs extends ZIOAppDefault {
  def run = for {
    args <- getArgs
    _ <-
      if (args.isEmpty)
        printLine("Please provide your name as an argument")
      else
        printLine(s"Hello, ${args.head}!")
  } yield ()
}
