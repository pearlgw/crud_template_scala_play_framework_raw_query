package controllers.api

import controllers.api.BaseApiController
import play.api.libs.json.*
import play.api.mvc.*
import requests.{CreateUserRequest, UpdateUserRequest}
import services.UserService

import javax.inject.*

@Singleton
class UserController @Inject()
(
  cc: ControllerComponents,
  service: UserService
) extends BaseApiController(cc) {
  def store() = Action(parse.json) { implicit request: Request[JsValue] =>
    try {
      request.body.validate[CreateUserRequest] match {
        case JsSuccess(data, _) =>
          service.createUser(data)
          successCustom(201, "User berhasil dibuat")

        case JsError(errors) =>
          error("Invalid request", 400)
      }

    } catch {
      case e: Exception =>
        e.printStackTrace()
        error("Gagal membuat user", 500)
    }
  }

  def index(): Action[AnyContent] = Action { implicit request =>
    try {
      val page = request.getQueryString("page").flatMap(_.toIntOption).getOrElse(1)
      val limit = request.getQueryString("limit").flatMap(_.toIntOption).getOrElse(10)
      val search = request.getQueryString("search")

      val (users, total) = service.getAllUsers(page, limit, search)

      success(
        "Berhasil menampilkan data",
        Json.toJson(users),
        Some(Json.obj(
          "page" -> page,
          "limit" -> limit,
          "total" -> total
        ))
      )

    } catch {
      case e: Exception =>
        e.printStackTrace()
        error("Internal Server Error")
    }
  }

  def show(id: String) = Action {
    try {
      service.getUserById(id) match {
        case Some(user) =>
          success("Berhasil menampilkan detail", Json.toJson(user))

        case None =>
          error("User tidak ditemukan", 404)
      }

    } catch {
      case e: Exception =>
        e.printStackTrace()
        error("Internal Server Error", 500)
    }
  }

  def update(id: String) = Action(parse.json) { implicit request: Request[JsValue] =>
    try {
      request.body.validate[UpdateUserRequest] match {

        case JsSuccess(data, _) =>
          val updated = service.updateUser(id, data)

          if (updated)
            successCustom(200, "User berhasil diupdate")
          else
            error("User tidak ditemukan", 404)

        case JsError(_) =>
          error("Invalid request", 400)
      }

    } catch {
      case e: Exception =>
        e.printStackTrace()
        error("Gagal update user", 500)
    }
  }

  def delete(id: String) = Action {
    try {
      val deleted = service.deleteUser(id)

      if (deleted)
        successCustom(200, "User berhasil dihapus")
      else
        error("User tidak ditemukan", 404)

    } catch {
      case e: Exception =>
        e.printStackTrace()
        error("Gagal menghapus user", 500)
    }
  }

  def viewIndex() = Action {
    Ok(views.html.index())
  }

  def viewShow(id: String) = Action {
    Ok(views.html.users.show(id))
  }
}
