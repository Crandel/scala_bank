package api.transactions

import scala.concurrent.Future
import akka.actor.ActorSystem
import javax.inject.{Inject, Singleton}
import play.api.libs.concurrent.CustomExecutionContext
import play.api.{Logger, MarkerContext}
import api.accounts.AccountRepository
import com.google.inject.ImplementedBy
import db.{TransactionData, TransactionId, Transactions}


class TransactionExecutionContext @Inject()(actorSystem: ActorSystem) extends CustomExecutionContext(actorSystem, "repository.dispatcher")
/**
  * A pure non-blocking interface for the PostRepository.
  */
@ImplementedBy(classOf[TransactionRepositoryImpl])
trait TransactionRepository {
  def create(data: TransactionData)(implicit mc: MarkerContext): Future[TransactionId]

  def list()(implicit mc: MarkerContext): Future[Iterable[TransactionData]]

  def get(id: String)(implicit mc: MarkerContext): Future[Option[TransactionData]]

  def update(data: TransactionData)(implicit mc: MarkerContext): Future[Boolean]

  def delete(id: String)(implicit mc: MarkerContext): Future[Boolean]

}


@Singleton
class TransactionRepositoryImpl @Inject()()(implicit ec: TransactionExecutionContext, implicit val ar: AccountRepository) extends TransactionRepository {

  private val logger = Logger(this.getClass)

  override def list()(implicit mc: MarkerContext): Future[Iterable[TransactionData]] = {
    Future {
      logger.trace(s"list: ")
      Transactions.list()
    }
  }

  override def get(id: String)(implicit mc: MarkerContext): Future[Option[TransactionData]] = {
    Future {
      logger.trace(s"get: id = $id")
      Transactions.get(id)
    }
  }

  def create(data: TransactionData)(implicit mc: MarkerContext): Future[TransactionId] = {
    Future {
      logger.trace(s"create: data = $data")
      Transactions.create(data)
    }
  }

  def update(data: TransactionData)(implicit mc: MarkerContext): Future[Boolean] = {
    Future {
      logger.info(s"update: data = $data")
      Transactions.update(data)
    }
  }

  def delete(id: String)(implicit mc: MarkerContext): Future[Boolean] = {
    Future {
      logger.info(s"delete: ")
      Transactions.delete(id)
    }
  }

}
