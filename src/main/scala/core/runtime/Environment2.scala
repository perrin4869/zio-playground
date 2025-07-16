package il.co.dotcore.myziotest

import zio._

object Environment2 extends App {

  val testableRuntime = Runtime(
    ZEnvironment[LoggingService, EmailService](
      LoggingServiceLive(),
      EmailServiceFake()
    ),
    FiberRefs.empty,
    RuntimeFlags.default
  )

  // val testableRuntime: Runtime[LoggingService with EmailService] =
  //   Runtime.default.withEnvironment {
  //     ZEnvironment[LoggingService, EmailService](
  //       LoggingServiceLive(),
  //       EmailServiceFake()
  //     )
  //   }

  Unsafe.unsafe { implicit u =>
    testableRuntime.unsafe
      .run(
        for {
          _ <- LoggingService.log("sending newsletter")
          _ <- EmailService.send("David", "Hi! Here is today's newsletter.")
        } yield ()
      )
      .getOrThrowFiberFailure()
  }

}
