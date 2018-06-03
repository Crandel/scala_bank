package api.accounts

import scala.concurrent.Future
import akka.actor.ActorSystem
import javax.inject.{Inject, Singleton}
import com.google.inject.ImplementedBy
import play.api.libs.concurrent.CustomExecutionContext
import play.api.{Logger, MarkerContext}
import db.{AccountData, AccountId, Accounts}
import api.users.UserRepository


class AccountExecutionContext @Inject()(actorSystem: ActorSystem) extends CustomExecutionContext(actorSystem, "repository.dispatcher")

@ImplementedBy(classOf[AccountRepositoryImpl])
trait AccountRepository {
  def list()(implicit mc: MarkerContext): Future[Iterable[AccountData]]

  def get(id: String)(implicit mc: MarkerContext): Future[Option[AccountData]]

  def create(data: AccountData)(implicit mc: MarkerContext): Future[AccountId]

  def update(data: AccountData)(implicit mc: MarkerContext): Future[Boolean]

  def delete(id: String)(implicit mc: MarkerContext): Future[Boolean]
}


@Singleton
class AccountRepositoryImpl @Inject()()(implicit ec: AccountExecutionContext,
                                        implicit val ur: UserRepository) extends AccountRepository{

  private val logger = Logger(this.getClass)

  override def list()(implicit mc: MarkerContext): Future[Iterable[AccountData]] = {
    Future {
      logger.info(s"list: ")
      Accounts.list()
    }
  }

  override def get(id: String)(implicit mc: MarkerContext): Future[Option[AccountData]] = {
    Future {
      logger.info(s"get: id = $id")
      Accounts.get(id)
    }
  }

  def create(data: AccountData)(implicit mc: MarkerContext): Future[AccountId] = {
    Future {
      logger.info(s"create: data = $data")
      Accounts.create(data)
    }
  }

  def update(data: AccountData)(implicit mc: MarkerContext): Future[Boolean] = {
    Future {
      logger.info(s"update: data = $data")
      Accounts.update(data)
    }
  }

  def delete(id: String)(implicit mc: MarkerContext): Future[Boolean] = {
    Future {
      logger.info(s"delete: ")
      Accounts.delete(id)
    }
  }
}
