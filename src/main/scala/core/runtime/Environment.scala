package il.co.dotcore.zio.playground.core.runtime

import zio._

trait LoggingService {
  def log(line: String): UIO[Unit]
}

object LoggingService {
  def log(line: String): URIO[LoggingService, Unit] =
    ZIO.serviceWithZIO[LoggingService](_.log(line))
}

trait EmailService {
  def send(user: String, content: String): Task[Unit]
}

object EmailService {
  def send(user: String, content: String): ZIO[EmailService, Throwable, Unit] =
    ZIO.serviceWithZIO[EmailService](_.send(user, content))
}

case class LoggingServiceLive() extends LoggingService {
  override def log(line: String): UIO[Unit] =
    ZIO.succeed(print(line))
}

case class EmailServiceFake() extends EmailService {
  override def send(user: String, content: String): Task[Unit] =
    ZIO.attempt(println(s"sending email to $user"))
}

object Environment extends ZIOAppDefault {

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

  override val runtime: Runtime[LoggingService with EmailService] =
    testableRuntime

  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] =
    ZLayer(
      ZIO.succeed(LoggingServiceLive())
    ) ++
      ZLayer(
        ZIO.succeed(EmailServiceFake())
      )

  def run =
    (for {
      _ <- LoggingService.log("sending newsletter")
      _ <- EmailService.send("David", "Hi! Here is today's newsletter.")
    } yield ()).provide(
      ZLayer(
        ZIO.succeed(LoggingServiceLive())
      ) ++
        ZLayer(
          ZIO.succeed(EmailServiceFake())
        )
    )

}
