package requests

import play.api.libs.json.*

case class CreateUserRequest
(
  nama: String,
  no_telepon: Option[String],
  alamat: Option[String]
)

object CreateUserRequest {
  implicit val reads: Reads[CreateUserRequest] = Json.reads[CreateUserRequest]
}

case class UpdateUserRequest
(
  nama: Option[String],
  no_telepon: Option[String],
  alamat: Option[String]
)

object UpdateUserRequest {
  implicit val reads: Reads[UpdateUserRequest] = Json.reads[UpdateUserRequest]
}