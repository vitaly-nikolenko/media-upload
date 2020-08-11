package com.softwaremill.bootzooka.media

import java.io.File

import cats.data.NonEmptyList
import com.softwaremill.bootzooka.http.Http
import com.softwaremill.bootzooka.infrastructure.Json._
import com.softwaremill.bootzooka.security.{ApiKey, Auth}
import com.softwaremill.bootzooka.util.ServerEndpoints
import monix.execution.Scheduler

class MediaApi(http: Http,
  auth: Auth[ApiKey],
  mediaService: MediaService,
  blockingScheduler: Scheduler) {

  import MediaApi._
  import http._

  private val MediaPath = "media"

  private val uploadScreenshotEndpoint = baseEndpoint.post
    .in(MediaPath / "screenshot")
    .in(multipartBody[UploadScreenshotRequest])
    .out(jsonBody[UploadScreenshotResponse])
    .serverLogic {
      case (request) =>
        (for {
          response <- mediaService.upload(request)
        } yield response).toOut.executeOn(blockingScheduler)
    }

  private val uploadScreenshotMockEndpoint = baseEndpoint.post
    .in(MediaPath / "mock")
    .in(multipartBody[UploadScreenshotRequest])
    .out(jsonBody[UploadScreenshotResponse])
    .serverLogic {
      case (request) =>
        (for {
          response <- mediaService.uploadMock(request)
        } yield response).toOut.executeOn(blockingScheduler)
    }

  val endpoints: ServerEndpoints =
    NonEmptyList
      .of(
        uploadScreenshotEndpoint,
        uploadScreenshotMockEndpoint
      )
      .map(_.tag("media"))

}


object MediaApi {

  case class UploadScreenshotRequest(name: String, contentType: String, screenshot: File)
  case class UploadScreenshotResponse(success: Boolean, message: String, screenshotId: String)

}
