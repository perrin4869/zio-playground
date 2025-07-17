package il.co.dotcore.zio.playground

import zio._
import zio.Console._

object MyUnsafeTest extends App {
  val runtime = Runtime.default

  val ret = Unsafe.unsafe { implicit unsafe =>
    runtime.unsafe
      .run(ZIO.attempt({
        println("Hello World!")
        2
      }))
      .getOrThrowFiberFailure()
  }

  println(s"Return value: ${ret}")
}
