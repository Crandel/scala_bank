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
  def map()(implicit mc: MarkerContext): Future[Map[Int, UserData]]

  def get(id: Int)(implicit mc: MarkerContext): Future[Option[UserData]]

  def create(data: UserData)(implicit mc: MarkerContext): Future[Int]

  def update(id: Int, data: UserData)(implicit mc: MarkerContext): Future[Boolean]

  def delete(id: Int)(implicit mc: MarkerContext): Future[Boolean]
}

@Singleton
class UserRepositoryImpl @Inject()()(implicit ec: UserExecutionContext)
    extends UserRepository {

  private val logger = Logger(this.getClass)

  override def map()(implicit mc: MarkerContext): Future[Map[Int, UserData]] = {
    Future {
      logger.trace(s"list: ")
      Users.map()
    }
  }

  override def get(id: Int)(implicit mc: MarkerContext): Future[Option[UserData]] = {
    Future {
      logger.info(s"get: id = $id")
      Users.get(id)
    }
  }

  def create(data: UserData)(implicit mc: MarkerContext): Future[Int] = {
    Future {
      logger.info(s"create: data = $data")
      Users.create(data)
    }
  }

  def update(id: Int, data: UserData)(implicit mc: MarkerContext): Future[Boolean] = {
    Future {
      logger.info(s"update: data = $data")
      Users.update(id, data)
    }
  }

  def delete(id: Int)(implicit mc: MarkerContext): Future[Boolean] = {
    Future {
      logger.info(s"delete: ")
      Users.delete(id)
    }
  }
}
