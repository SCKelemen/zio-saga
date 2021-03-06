package com.vladkopanev.zio.saga.example.client

import java.util.UUID

import com.vladkopanev.zio.saga.example.TaskC
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import zio.Task

trait OrderServiceClient {

  def closeOrder(userId: UUID, orderId: BigInt, traceId: String): TaskC[Unit]

  def reopenOrder(userId: UUID, orderId: BigInt, traceId: String): TaskC[Unit]
}

class OrderServiceClientStub(logger: Logger[Task], maxRequestTimeout: Int, flaky: Boolean) extends OrderServiceClient {

  override def closeOrder(userId: UUID, orderId: BigInt, traceId: String): TaskC[Unit] =
    for {
      _ <- randomSleep(maxRequestTimeout)
      _ <- randomFail("closeOrder").when(flaky)
      _ <- logger.info(s"Order #$orderId closed")
    } yield ()

  override def reopenOrder(userId: UUID, orderId: BigInt, traceId: String): TaskC[Unit] =
    for {
      _ <- randomSleep(maxRequestTimeout)
      _ <- randomFail("reopenOrder").when(flaky)
      _ <- logger.info(s"Order #$orderId reopened")
    } yield ()
}

object OrderServiceClientStub {

  import zio.interop.catz._

  def apply(maxRequestTimeout: Int, flaky: Boolean): Task[OrderServiceClientStub] =
    Slf4jLogger.create[Task].map(new OrderServiceClientStub(_, maxRequestTimeout, flaky))
}
