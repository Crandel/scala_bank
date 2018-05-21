package api.users

import scala.concurrent.Future
import scala.collection.mutable

import akka.actor.ActorSystem
import javax.inject.{Inject, Singleton}
import com.google.inject.ImplementedBy
import play.api.libs.concurrent.CustomExecutionContext
import play.api.{Logger, MarkerContext}
import play.api.libs.json._

final case class UserData(id: UserId,
                          name: String,
                          login: String,
                          password: String)


class UserId private (val underlying: Int) extends AnyVal {
  override def toString: String = underlying.toString
}

object UserId {
  private var counter: Int = 0
  private var current_id: Int = 0

  implicit val userWrites = new Writes[UserId] {
    def writes(user: UserId) = Json.obj(
      "id" -> user.toString
    )
  }

  def apply(raw: String = ""): UserId = {
    if (raw == "" ){
      current_id = counter
      counter += 1
    } else {
      current_id = Integer.parseInt(raw)
    }
    new UserId(current_id)
  }
}


class UserExecutionContext @Inject()(actorSystem: ActorSystem)
    extends CustomExecutionContext(actorSystem, "repository.dispatcher")

/**
  * A pure non-blocking interface for the PostRepository.
  */
@ImplementedBy(classOf[UserRepositoryImpl])
trait UserRepository {
  def list()(implicit mc: MarkerContext): Future[Iterable[UserData]]

  def get(id: UserId)(implicit mc: MarkerContext): Future[Option[UserData]]

  def create(data: UserData)(implicit mc: MarkerContext): Future[UserId]

  def update(id: UserId, data: UserData)(implicit mc: MarkerContext): Boolean

  def delete(id: UserId)(implicit mc: MarkerContext): Boolean
}
@Singleton
class UserRepositoryImpl @Inject()()(implicit ec: UserExecutionContext)
    extends UserRepository {

  private val logger = Logger(this.getClass)

  private var userList = mutable.MutableList(
    UserData(UserId(), "name 1", "login 1", "password 1"),
    UserData(UserId(), "name 2", "login 2", "password 2"),
    UserData(UserId(), "name 3", "login 3", "password 3"),
    UserData(UserId(), "name 4", "login 4", "password 4"),
    UserData(UserId(), "name 5", "login 5", "password 5")
  )

  override def list()(
      implicit mc: MarkerContext): Future[Iterable[UserData]] = {
    Future {
      logger.trace(s"list: ")
      userList
    }
  }

  override def get(id: UserId)(
      implicit mc: MarkerContext): Future[Option[UserData]] = {
    Future {
      logger.trace(s"get: id = $id")
      userList.find(user => user.id == id)
    }
  }

  def create(data: UserData)(implicit mc: MarkerContext): Future[UserId] = {
    Future {
      logger.trace(s"create: data = $data")
      userList += data
      data.id
    }
  }

  def update(id: UserId, data: UserData)(implicit mc: MarkerContext): Boolean = {
    logger.trace(s"update: data = $data")
    userList = userList.filter(user => user.id != id)
    userList += data
    true
  }

  def delete(id: UserId)(implicit mc: MarkerContext): Boolean = {
    logger.trace(s"delete: ")
    userList = userList.filter(user => user.id != id)
    true
  }
}
