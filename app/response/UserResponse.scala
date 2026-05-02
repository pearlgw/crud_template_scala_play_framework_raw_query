package response

import play.api.libs.json.*

case class UserResponse
(
  id: String,
  nama: String,
  no_telepon: Option[String],
  alamat: Option[String],
  created_at: Option[String]
)

object UserResponse {
  implicit val writes: OWrites[UserResponse] = Json.writes[UserResponse]
}