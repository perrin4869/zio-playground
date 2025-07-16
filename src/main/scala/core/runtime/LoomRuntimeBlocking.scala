package il.co.dotcore.myziotest

import zio._

object LoomRuntimeBlocking extends ZIOAppDefault {
  override val bootstrap = Runtime.enableLoomBasedBlockingExecutor

  override def run = ZIO.attempt {
    println(
      s"Task running on a virtual-thread: ${Thread.currentThread().getName()}"
    )
  }
}
