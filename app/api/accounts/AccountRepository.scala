package api.accounts

import scala.concurrent.Future
import akka.actor.ActorSystem
import javax.inject.{Inject, Singleton}
import com.google.inject.ImplementedBy
import play.api.libs.concurrent.CustomExecutionContext
import play.api.{Logger, MarkerContext}
import db.{AccountData, Accounts}
import api.users.UserRepository


class AccountExecutionContext @Inject()(actorSystem: ActorSystem) extends CustomExecutionContext(actorSystem, "repository.dispatcher")

@ImplementedBy(classOf[AccountRepositoryImpl])
trait AccountRepository {
  def map()(implicit mc: MarkerContext): Future[Map[Int, AccountData]]

  def get(id: Int)(implicit mc: MarkerContext): Future[Option[AccountData]]

  def create(data: AccountData)(implicit mc: MarkerContext): Future[Option[Int]]

  def update(id: Int, data: AccountData)(implicit mc: MarkerContext): Future[Boolean]

  def delete(id: Int)(implicit mc: MarkerContext): Future[Boolean]
}


@Singleton
class AccountRepositoryImpl @Inject()()(implicit ec: AccountExecutionContext,
                                        implicit val ur: UserRepository) extends AccountRepository{

  private val logger = Logger(this.getClass)

  override def map()(implicit mc: MarkerContext): Future[Map[Int, AccountData]] = {
    Future {
      logger.info(s"list: ")
      Accounts.map()
    }
  }

  override def get(id: Int)(implicit mc: MarkerContext): Future[Option[AccountData]] = {
    Future {
      logger.info(s"get: id = $id")
      Accounts.get(id)
    }
  }

  def create(data: AccountData)(implicit mc: MarkerContext): Future[Option[Int]] = {
    Future {
      logger.info(s"create: data = $data")
      Accounts.create(data)
    }
  }

  def update(id: Int, data: AccountData)(implicit mc: MarkerContext): Future[Boolean] = {
    Future {
      logger.info(s"update: data = $data")
      Accounts.update(id, data)
    }
  }

  def delete(id: Int)(implicit mc: MarkerContext): Future[Boolean] = {
    Future {
      logger.info(s"delete: ")
      Accounts.delete(id)
    }
  }
}
