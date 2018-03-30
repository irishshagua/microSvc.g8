package de.zalando.spp.brands.auth

import akka.http.scaladsl.server.Directive1
import de.zalando.spp.brands.auth.Models.TokenAndPermission

trait Authentication {

  def scoped(): Directive1[TokenAndPermission]
}
