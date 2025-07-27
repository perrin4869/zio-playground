package il.co.dotcore.zio.playground.streaming.zchannel.operations

import zio._
import zio.stream._

object Sequencing extends ZIOAppDefault {
  override def run = ZChannel
    .fromZIO(
      Console.readLine("Please enter a number: ").map(_.toInt)
    )
    .flatMap {
      case n if n < 0 => ZChannel.fail("Number must be positive")
      case n          => ZChannel.writeAll((0 to n): _*)
    }
    .runCollect
    .debug
  // Sample Output:
  // Please enter a number: 5
  // (Chunk(0,1,2,3,4,5),())
}
