package api.users

import akka.actor.ActorSystem
import javax.inject.Inject
import play.api.libs.concurrent.CustomExecutionContext
import play.api.{Logger, MarkerContext}

import scala.concurrent.Future

final case class UserData(id: UserId, name: String, login: String, password: String)

class UserId private (val underlying: Int) extends AnyVal {
  override def toString: String = underlying.toString
}

object UserId {
  def apply(raw: String): UserId = {
    require(raw != null)
    new UserId(Integer.parseInt(raw))
  }
}

class UserExecutionContext @Inject()(actorSystem: ActorSystem) extends CustomExecutionContext(actorSystem, "repository.dispatcher")
/**
  * A pure non-blocking interface for the PostRepository.
  */
trait UserRepository {
  def create(data: UserData)(implicit mc: MarkerContext): Future[UserId]

  def list()(implicit mc: MarkerContext): Future[Iterable[UserData]]

  def get(id: UserId)(implicit mc: MarkerContext): Future[Option[UserData]]
}


@Singleton
class UserRepositoryImpl @Inject()()(implicit ec: UserExecutionContext) extends UserRepository {

  private val logger = Logger(this.getClass)

  private val userList = List(
    UserData(UserId("1"), "name 1", "login 1", "password 1"),
    UserData(UserId("2"), "name 2", "login 2", "password 2"),
    UserData(UserId("3"), "name 3", "login 3", "password 3"),
    UserData(UserId("4"), "name 4", "login 4", "password 4"),
    UserData(UserId("5"), "name 5", "login 5", "password 5")
  )

  override def list()(implicit mc: MarkerContext): Future[Iterable[UserData]] = {
    Future {
      logger.trace(s"list: ")
      userList
    }
  }

  override def get(id: UserId)(implicit mc: MarkerContext): Future[Option[UserData]] = {
    Future {
      logger.trace(s"get: id = $id")
      userList.find(user => user.id == id)
    }
  }

  def create(data: UserData)(implicit mc: MarkerContext): Future[UserId] = {
    Future {
      logger.trace(s"create: data = $data")
      data.id
    }
  }

}
