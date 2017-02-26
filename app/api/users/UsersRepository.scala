package api.users

import javax.inject.{Inject, Singleton}

import scala.concurrent.Future

final case class UserData(id: UserId, title: String, body: String)

class UserId private (val underlying: Int) extends AnyVal {
  override def toString: String = underlying.toString
}

object UserId {
  def apply(raw: String): UserId = {
    require(raw != null)
    new UserId(Integer.parseInt(raw))
  }
}

/**
  * A pure non-blocking interface for the UserRepository.
  */
trait UserRepository {
  def create(data: UserData): Future[UserId]

  def list(): Future[Iterable[UserData]]

  def get(id: UserId): Future[Option[UserData]]
}

/**
  * A trivial implementation for the Post Repository.
  */
@Singleton
class UserRepositoryImpl @Inject() extends UserRepository {

  private val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)

  private val postList = List(
    UserData(UserId("1"), "title 1", "blog post 1"),
    UserData(UserId("2"), "title 2", "blog post 2"),
    UserData(UserId("3"), "title 3", "blog post 3"),
    UserData(UserId("4"), "title 4", "blog post 4"),
    UserData(UserId("5"), "title 5", "blog post 5")
  )

  override def list(): Future[Iterable[UserData]] = {
    Future.successful {
      logger.trace(s"list: ")
      postList
    }
  }

  override def get(id: UserId): Future[Option[UserData]] = {
    Future.successful {
      logger.trace(s"get: id = $id")
      postList.find(post => post.id == id)
    }
  }

  def create(data: UserData): Future[UserId] = {
    Future.successful {
      logger.trace(s"create: data = $data")
      data.id
    }
  }

}
