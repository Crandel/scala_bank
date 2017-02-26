package api.users

import javax.inject.{Inject, Provider}

import scala.concurrent.{ExecutionContext, Future}

import play.api.libs.json._

/**
  * DTO for displaying post information.
  */
case class UsersResource(id: String, link: String, title: String, body: String)

object UsersResource {

  /**
    * Mapping to write a UsersResource out as a JSON value.
    */
  implicit val implicitWrites = new Writes[UsersResource] {
    def writes(user: UsersResource): JsValue = {
      Json.obj(
        "id" -> user.id,
        "link" -> user.link,
        "title" -> user.title,
        "body" -> user.body
      )
    }
  }
}

/**
  * Controls access to the backend data, returning [[UsersResource]]
  */
class UsersResourceHandler @Inject()(
    routerProvider: Provider[UsersRouter],
    userRepository: UserRepository)(implicit ec: ExecutionContext) {

  def create(userInput: UserFormInput): Future[UsersResource] = {
    val data = UserData(UserId("999"), userInput.title, userInput.body)
    // We don't actually create the post, so return what we have
    userRepository.create(data).map { id =>
      UserstePostResource(data)
    }
  }

  def lookup(id: String): Future[Option[UsersResource]] = {
    val userFuture = userRepository.get(UserId(id))
    userFuture.map { maybeUserData =>
      maybeUserData.map { userData =>
        UserstePostResource(userData)
      }
    }
  }

  def find: Future[Iterable[UsersResource]] = {
    userRepository.list().map { userDataList =>
      userDataList.map(userData => UserstePostResource(userData))
    }
  }

  private def UserstePostResource(u: UserData): UsersResource = {
    UsersResource(u.id.toString, routerProvider.get.link(u.id), u.title, u.body)
  }

}
