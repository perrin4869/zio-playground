package il.co.dotcore.zio.playground.overview.running_effects

import zio._
import zio.Console._

object MyRuntimeTest extends App {
  val myRuntime: Runtime[Int] =
    Runtime(ZEnvironment[Int](42), FiberRefs.empty, RuntimeFlags.default)

  Unsafe.unsafe { implicit unsafe =>
    myRuntime.unsafe
      .run(printLine("Hello World!"))
      .getOrThrowFiberFailure()
  }
}
