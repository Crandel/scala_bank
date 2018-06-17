package api.users

import db.UserData
import javax.inject.{Inject, Provider}
import play.api.MarkerContext

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._

/**
  * DTO for displaying user information.
  */
case class UserResource(id: Int,
                        name: String,
                        login: String,
                        password: String)

object UserResource {

  /**
    * Mapping to write a UserResource out as a JSON value.
    */
  implicit val implicitWrites: Writes[UserResource] = new Writes[UserResource] {
    def writes(user: UserResource): JsValue = {
      Json.obj(
        "id" -> user.id,
        "name" -> user.name,
        "login" -> user.login,
        "password" -> user.password
      )
    }
  }
}

/**
  * Controls access to the backend data, returning [[UserResource]]
  */
class UserResourceHandler @Inject()(
    routerProvider: Provider[UsersRouter],
    userRepository: UserRepository)(implicit ec: ExecutionContext) {

  // get users list
  def find(id: Int)(
    implicit mc: MarkerContext): Future[Option[UserResource]] = {
    val postFuture = userRepository.get(id)
    postFuture.map { maybeUserData =>
      maybeUserData.map { userData =>
        createUserResource(id, userData)
      }
    }
  }

  // get single user
  def findAll(implicit mc: MarkerContext): Future[Iterable[UserResource]] = {
    userRepository.map().map { userDataList =>
      userDataList.map(userData => createUserResource(userData._1, userData._2))
    }
  }

  // create new user
  def create(userInput: UserFormInput)(
    implicit mc: MarkerContext): Future[Int] = {
    val data = UserData(
      userInput.name,
      userInput.login,
      userInput.password)
    userRepository.create(data)
  }

  // update existing user
  def update(id: Int, userInput: UserFormInput)(
    implicit mc: MarkerContext): Future[Boolean]= {
    val data = UserData(
      userInput.name,
      userInput.login,
      userInput.password)
    userRepository.update(id, data)
  }

  // delete existing user
  def delete(id: Int)(
    implicit mc: MarkerContext): Future[Boolean]= {
    userRepository.delete(id)
  }

  private def createUserResource(id: Int, u: UserData): UserResource = {
    UserResource(id, u.name, u.login, u.password)
  }
}
