package com.softwaremill.bootzooka.media

import com.softwaremill.bootzooka.http.Http
import com.softwaremill.bootzooka.security.{ApiKey, Auth}
import com.softwaremill.bootzooka.util.BaseModule
import monix.eval.Task
import monix.execution.Scheduler
import sttp.client.{NothingT, SttpBackend}

trait MediaModule extends BaseModule {

  lazy val mediaService = new MediaService(baseSttpBackend)
  lazy val mediaApi = new MediaApi(http, apiKeyAuth, mediaService, blockingIO)

  def baseSttpBackend: SttpBackend[Task, Nothing, NothingT]
  def http: Http
  def apiKeyAuth: Auth[ApiKey]
  def blockingIO: Scheduler
}
