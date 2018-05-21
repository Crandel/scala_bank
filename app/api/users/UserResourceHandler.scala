package api.users

import javax.inject.{Inject, Provider}

import play.api.MarkerContext

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._

/**
  * DTO for displaying user information.
  */
case class UserResource(id: String,
                        name: String,
                        login: String,
                        password: String)

object UserResource {

  /**
    * Mapping to write a UserResource out as a JSON value.
    */
  implicit val implicitWrites = new Writes[UserResource] {
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
  def lookup(id: String)(
    implicit mc: MarkerContext): Future[Option[UserResource]] = {
    val postFuture = userRepository.get(UserId(id))
    postFuture.map { maybeUserData =>
      maybeUserData.map { userData =>
        createUserResource(userData)
      }
    }
  }

  // get single user
  def find(implicit mc: MarkerContext): Future[Iterable[UserResource]] = {
    userRepository.list().map { userDataList =>
      userDataList.map(userData => createUserResource(userData))
    }
  }

  // create new user
  def create(userInput: UserFormInput)(
    implicit mc: MarkerContext): Future[UserId] = {
    val data = UserData(UserId(),
      userInput.name,
      userInput.login,
      userInput.password)
    // We don't actually create the post, so return what we have
    userRepository.create(data)
  }

  // update existing user
  def update(id: String, userInput: UserFormInput)(
    implicit mc: MarkerContext): Future[Boolean]= {
    val userIDObj = UserId(id)
    val data = UserData(userIDObj,
      userInput.name,
      userInput.login,
      userInput.password)
    // We don't actually create the post, so return what we have
    userRepository.update(userIDObj, data)
  }

  // delete existing user
  def delete(id: String)(
    implicit mc: MarkerContext): Future[Boolean]= {
    val userIdObj = UserId(id)
    // We don't actually create the post, so return what we have
    userRepository.delete(userIdObj)
  }

  private def createUserResource(u: UserData): UserResource = {
    UserResource(u.id.toString, u.name, u.login, u.password)
  }
}
