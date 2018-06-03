package api.users

import scala.concurrent.Future

import akka.actor.ActorSystem
import javax.inject.{Inject, Singleton}
import com.google.inject.ImplementedBy
import play.api.libs.concurrent.CustomExecutionContext
import play.api.{Logger, MarkerContext}
import db._



class UserExecutionContext @Inject()(actorSystem: ActorSystem)
    extends CustomExecutionContext(actorSystem, "repository.dispatcher")

/**
  * A pure non-blocking interface for the PostRepository.
  */
@ImplementedBy(classOf[UserRepositoryImpl])
trait UserRepository {
  def list()(implicit mc: MarkerContext): Future[Iterable[UserData]]

  def get(id: String)(implicit mc: MarkerContext): Future[Option[UserData]]

  def create(data: UserData)(implicit mc: MarkerContext): Future[UserId]

  def update(data: UserData)(implicit mc: MarkerContext): Future[Boolean]

  def delete(id: String)(implicit mc: MarkerContext): Future[Boolean]
}

@Singleton
class UserRepositoryImpl @Inject()()(implicit ec: UserExecutionContext)
    extends UserRepository {

  private val logger = Logger(this.getClass)

  override def list()(implicit mc: MarkerContext): Future[Iterable[UserData]] = {
    Future {
      logger.trace(s"list: ")
      Users.list()
    }
  }

  override def get(id: String)(implicit mc: MarkerContext): Future[Option[UserData]] = {
    Future {
      logger.info(s"get: id = $id")
      Users.get(id)
    }
  }

  def create(data: UserData)(implicit mc: MarkerContext): Future[UserId] = {
    Future {
      logger.info(s"create: data = $data")
      Users.create(data)
    }
  }

  def update(data: UserData)(implicit mc: MarkerContext): Future[Boolean] = {
    Future {
      logger.info(s"update: data = $data")
      Users.update(data)
    }
  }

  def delete(id: String)(implicit mc: MarkerContext): Future[Boolean] = {
    Future {
      logger.info(s"delete: ")
      Users.delete(id)
    }
  }
}
