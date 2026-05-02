package services

import javax.inject._
import repository.UserRepository
import requests.CreateUserRequest
import requests.UpdateUserRequest
import response.UserResponse

@Singleton
class UserService @Inject()(repo: UserRepository) {
  def getAllUsers() = {
    repo.findAll()
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