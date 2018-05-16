package api.users

import javax.inject.{Inject, Provider}

import play.api.MarkerContext

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._

/**
  * DTO for displaying post information.
  */
case class UserResource(id: String, name: String, login: String, password: String)

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
                                     postRepository: UserRepository)(implicit ec: ExecutionContext) {

  def create(postInput: UserFormInput)(implicit mc: MarkerContext): Future[UserResource] = {
    val data = UserData(UserId("999"), postInput.name, postInput.login, postInput.password)
    // We don't actually create the post, so return what we have
    postRepository.create(data).map { id =>
      createUserResource(data)
    }
  }

  def lookup(id: String)(implicit mc: MarkerContext): Future[Option[UserResource]] = {
    val postFuture = postRepository.get(UserId(id))
    postFuture.map { maybeUserData =>
      maybeUserData.map { postData =>
        createUserResource(postData)
      }
    }
  }

  def find(implicit mc: MarkerContext): Future[Iterable[UserResource]] = {
    postRepository.list().map { postDataList =>
      postDataList.map(postData => createUserResource(postData))
    }
  }

  private def createUserResource(p: UserData): UserResource = {
    UserResource(p.id.toString, p.name, p.login, p.password)
  }

}
