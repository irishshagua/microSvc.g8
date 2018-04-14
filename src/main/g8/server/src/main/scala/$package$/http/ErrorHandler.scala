package $package$.http

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import $package$.http.Models.{CustomValidationRejection, ProblemJson}
import org.slf4j.{Logger, LoggerFactory}

import scala.util.control.NonFatal

object ErrorHandler {

  val logger: Logger = LoggerFactory.getLogger("ErrorHandler")

  implicit def serviceRejectionHandler: RejectionHandler =
    RejectionHandler
      .newBuilder()
      .handle {
        case r: CustomValidationRejection        => completeWithProblemJson(r.code, r.message)
        case r: ValidationRejection              => completeWithProblemJson(BadRequest, r.message)
        case r: MalformedRequestContentRejection => completeWithProblemJson(BadRequest, r.message)
        case r: MalformedQueryParamRejection     => completeWithProblemJson(BadRequest, r.errorMsg)
        case r: MissingQueryParamRejection       => completeWithProblemJson(BadRequest, s"\${r.parameterName} Query Param is required")
        case _: MissingHeaderRejection           => complete(HttpResponse(PreconditionRequired))
        case _: AuthenticationFailedRejection    => complete(HttpResponse(Unauthorized))
        case _: MethodRejection                  => complete(HttpResponse(Unauthorized))
      }
      .handleNotFound {
        extractUnmatchedPath { _ =>
          complete(HttpResponse(Unauthorized))
        }
      }
      .result()

  implicit def exceptionHandler: ExceptionHandler = ExceptionHandler {
    case NonFatal(e: Throwable) =>
      extractRequest { req =>
        logger.error(s"Request (Server Side Error): \${req.method} : \${req.uri}}", e)
        complete(HttpResponse(InternalServerError))
      }
  }

  def completeWithProblemJson(error: StatusCode, msg: String): StandardRoute = {
    import io.circe.generic.auto._
    import io.circe.syntax._

    if (error.defaultMessage() != msg) {
      complete(
        HttpResponse(
          error,
          entity = HttpEntity(ProblemJson(title = error.reason, status = error.intValue, detail = msg).asJson.toString)
            .withContentType(
              ContentType.WithFixedCharset(MediaType.customWithFixedCharset("application", "problem+json", HttpCharsets.`UTF-8`)))
        ))
    } else
      complete(HttpResponse(error))
  }
}
