package controllers.api

import play.api.libs.json.*
import play.api.mvc.*

abstract class BaseApiController(cc: ControllerComponents) extends AbstractController(cc) {

  def success(message: String, data: JsValue) = {
    Ok(Json.obj(
      "status" -> 200,
      "message" -> message,
      "data" -> data
    ))
  }

  def successCustom(statusCode: Int = 200, message: String) = {
    Ok(Json.obj(
      "status" -> statusCode,
      "message" -> message,
    ))
  }

  def error(message: String, code: Int = 500) = {
    Status(code)(Json.obj(
      "status" -> code,
      "message" -> message
    ))
  }
}