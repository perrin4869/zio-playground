package il.co.dotcore.zio.playground.core.runtime

import zio._

object LoomRuntimeBlocking extends ZIOAppDefault {
  override val bootstrap = Runtime.enableLoomBasedBlockingExecutor

  override def run = ZIO.attempt {
    println(
      s"Task running on a virtual-thread: ${Thread.currentThread().getName()}"
    )
  }
}
