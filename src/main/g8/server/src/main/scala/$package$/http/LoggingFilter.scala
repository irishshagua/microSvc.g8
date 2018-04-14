package $package$.http

import akka.event.LoggingAdapter
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.RouteResult.{Complete, Rejected}
import akka.http.scaladsl.server.directives.{DebuggingDirectives, LoggingMagnet}
import akka.http.scaladsl.server.{Route, RouteResult}

object LoggingFilter {

  sealed trait ResponseClassification
  case object Normal          extends ResponseClassification
  case object ClientSideError extends ResponseClassification
  case object ServerSideError extends ResponseClassification

  def loggingFilter(routes: Route): Route =
    DebuggingDirectives.logRequestResult(LoggingMagnet(log => {
      val requestTimestamp = System.currentTimeMillis()
      logRequestResponseDetails(log, requestTimestamp)
    }))(routes)

  def logRequestResponseDetails(log: LoggingAdapter, requestTimestamp: Long)(req: HttpRequest)(res: RouteResult): Unit = {
    res match {
      case Complete(resp) =>
        responseType(resp.status) match {
          case Normal =>
            log.info(s"HTTP[\${createRequestString(req)}] took \${duration(requestTimestamp)}ms and returned \${resp.status}")
          case ClientSideError =>
            log.warning(
              s"HTTP (4xx) [\${createRequestString(req)}] took \${duration(requestTimestamp)}ms and returned \${resp.status}")
          case ServerSideError =>
            log.error(
              s"HTTP (5xx) [\${createRequestString(req)}] took \${duration(requestTimestamp)}ms and returned \${resp.status}")
        }
      case Rejected(rejections) =>
        log.warning(s"HTTP (4xx) [\${createRequestString(req)}] took \${duration(requestTimestamp)}ms and returned \$rejections")
    }
  }

  private def createRequestString(req: HttpRequest): String =
    s"\${req.method.value} \${req.uri.path}?\${req.uri.rawQueryString.getOrElse("")}"

  def responseType(status: StatusCode): ResponseClassification = status.intValue() match {
    case i: Int if i < 400 => Normal
    case i: Int if i < 500 => ClientSideError
    case _                 => ServerSideError
  }

  def duration(startTime: Long): Long =
    System.currentTimeMillis() - startTime
}
