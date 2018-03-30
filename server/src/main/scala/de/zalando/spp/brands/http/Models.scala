package de.zalando.spp.brands.http

import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.model.headers.ETag
import akka.http.scaladsl.server.RejectionWithOptionalCause

object Models {

  case class ProblemJson(`type`: Option[String] = None,
                         title: String,
                         status: Int,
                         detail: String,
                         instance: Option[String] = None)

  case class CustomValidationRejection(code: StatusCode, message: String, cause: Option[Throwable] = None)
      extends RejectionWithOptionalCause

  case class HttpResponseData(status: StatusCode, message: String, eTag: Option[ETag])

  object HttpResponseData {

    def apply(status: StatusCode)                                     = new HttpResponseData(status, status.defaultMessage(), None)
    def apply(status: StatusCode, eTag: Option[ETag])                 = new HttpResponseData(status, status.defaultMessage(), eTag)
    def apply(status: StatusCode, message: String)                    = new HttpResponseData(status, message, None)
    def apply(status: StatusCode, message: String, tag: Option[ETag]) = new HttpResponseData(status, message, tag)
  }
}
