package de.zalando.spp.brands.http.routes

import akka.http.scaladsl.server.{Directive0, Directives, Route}
import kamon.Kamon

trait Controller extends Directives  {

  def limitedReadRoutes: Route
  def readOnlyRoutes: Route
  def readWriteRoutes: Route

  def metricName(name: String, tags: Map[String, String] = Map.empty): Directive0 = mapRequest { req ⇒
    val operationSpan = Kamon.currentSpan()
    operationSpan.setOperationName(s"$name.${req.method.value}")
    tags.foreach { case (key, value) ⇒ operationSpan.tag(key, value) }
    req
  }
}
