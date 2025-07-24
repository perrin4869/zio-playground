package il.co.dotcore.zio.playground.streaming.zstream.error_handling

import zio._
import zio.Console._
import zio.stream._

object RecoverFromSome extends ZIOAppDefault {

  val s1 = ZStream(1, 2, 3) ++ ZStream.fail("Oh! Error!") ++ ZStream(4, 5)
  val s2 = ZStream(7, 8, 9)
  val stream = s1.catchSome { case "Oh! Error!" =>
    s2
  }
  // Output: 1, 2, 3, 7, 8, 9

  object CatchSomeCause {
    import zio.Cause._

    val s1 =
      ZStream(1, 2, 3) ++ ZStream.dieMessage("Oh! Boom!") ++ ZStream(4, 5)
    val s2 = ZStream(7, 8, 9)
    val stream = s1.catchSomeCause { case Die(value, _) =>
      s2
    // case Stackless(cause, stackless) => s2
    }
  }

  override def run = for {
    _ <- print("stream: ") *>
      stream.runForeachChunk(printLine(_)) *> print("\n")
    _ <- print("CatchSomeCause.stream: ") *>
      CatchSomeCause.stream.runForeachChunk(printLine(_)) *> print("\n")
  } yield ()
}
