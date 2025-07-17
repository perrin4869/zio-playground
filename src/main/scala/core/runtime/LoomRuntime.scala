package il.co.dotcore.zio.playground.core.runtime

import zio._

object LoomRuntime extends ZIOAppDefault {
  override val bootstrap = Runtime.enableLoomBasedExecutor

  override def run = ZIO.attempt {
    println(
      s"Task running on a virtual-thread: ${Thread.currentThread().getName()}"
    )
  }
}
