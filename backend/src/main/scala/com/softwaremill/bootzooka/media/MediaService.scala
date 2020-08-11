package com.softwaremill.bootzooka.media

import com.softwaremill.bootzooka.media.MediaApi.{UploadScreenshotRequest, UploadScreenshotResponse}
import com.typesafe.scalalogging.StrictLogging
import io.circe.generic.auto._
import monix.eval.Task
import sttp.client.circe.asJson
import sttp.client.{Identity, NothingT, RequestT, ResponseError, SttpBackend, basicRequest, multipartFile, _}

class MediaService(sttpBackend: SttpBackend[Task, Nothing, NothingT]) extends StrictLogging {

  private def runRequest[E, T](request: RequestT[Identity, Either[E, T], Nothing], f: E => T): Task[T] =
    sttpBackend.send(request).map { response =>
      response.body match {
        case Right(body) => body
        case Left(error) => f(error)
      }
    }

  def upload(request: UploadScreenshotRequest): Task[UploadScreenshotResponse] =
    runRequest(basicRequest
      .multipartBody(
        multipartFile("screenshot", request.screenshot)
          .contentType(request.contentType)
          .fileName(request.name)
      ).post(uri"http://localhost:8080/upload")
      .response(asJson[UploadScreenshotResponse]),
      (e: ResponseError[io.circe.Error]) => UploadScreenshotResponse(success = false, e.getMessage, ""))

  def uploadMock(request: UploadScreenshotRequest): Task[UploadScreenshotResponse] =
    Task {
      logger.info(s"received screenshot ${request.name} for upload")
      Thread.sleep(30000)
      UploadScreenshotResponse(success = true, "", "")
    }
}

