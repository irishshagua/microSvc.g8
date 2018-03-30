package de.zalando.spp.brands.auth

import enumeratum._

object Models {

  sealed trait Permission extends EnumEntry
  object Permission extends Enum[Permission] {
    val values = findValues

    case object LIMITED_READ  extends Permission
    case object READ_WRITE    extends Permission
    case object READ_ONLY     extends Permission
    case object NO_PERMISSION extends Permission
  }

  case class TokenInfo(uid: String, realm: String, scope: Seq[String], expiresIn: Int)
  case class TokenAndPermission(token: TokenInfo, permission: Permission)
}
