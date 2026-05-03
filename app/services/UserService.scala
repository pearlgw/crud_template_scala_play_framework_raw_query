package services

import repository.UserRepository
import requests.{CreateUserRequest, UpdateUserRequest}
import response.UserResponse

import javax.inject.*

@Singleton
class UserService @Inject()(repo: UserRepository) {
  def getAllUsers(page: Int, limit: Int, search: Option[String]) = {
    repo.findAll(page, limit, search)
  }

  def createUser(req: CreateUserRequest): Int = {
    repo.create(req)
  }

  def getUserById(id: String): Option[UserResponse] = {
    repo.findById(id)
  }

  def updateUser(id: String, req: UpdateUserRequest): Boolean = {
    repo.update(id, req) > 0
  }

  def deleteUser(id: String): Boolean = {
    repo.delete(id) > 0
  }
}