package il.co.dotcore.zio.playground.streaming.zchannel.composing

import zio._
import zio.stream.ZChannel

import java.io.IOException

object EchoChannelForever extends ZIOAppDefault {
  val producer: ZChannel[Any, Any, Any, Any, IOException, String, Nothing] =
    ZChannel
      .fromZIO(Console.readLine("Please enter some text: "))
      .flatMap(i => ZChannel.write(i) *> producer)

  val consumer: ZChannel[Any, Any, String, Any, IOException, Nothing, Unit] =
    ZChannel.readWith(
      (i: String) =>
        i match {
          case "exit" => ZChannel.unit
          case _ =>
            ZChannel.fromZIO(Console.printLine("Consumed: " + i)) *> consumer
        },
      (_: Any) => ZChannel.unit,
      (_: Any) => ZChannel.unit
    )

  def run = (producer >>> consumer).run
}

// Output:
// Please enter some text: Foo
// Consumed: Foo
// Please enter some text: Bar
// Consumed: Bar
// Please enter some text: Baz
// Consumed: Baz
// Please enter some text: exit
