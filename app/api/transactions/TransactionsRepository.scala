package api.transactions

import scala.concurrent.Future
import akka.actor.ActorSystem
import javax.inject.{Inject, Singleton}
import play.api.libs.concurrent.CustomExecutionContext
import play.api.{Logger, MarkerContext}
import api.accounts.AccountRepository
import com.google.inject.ImplementedBy
import db.{TransactionData, Transactions}


class TransactionExecutionContext @Inject()(actorSystem: ActorSystem) extends CustomExecutionContext(actorSystem, "repository.dispatcher")
/**
  * A pure non-blocking interface for the PostRepository.
  */
@ImplementedBy(classOf[TransactionRepositoryImpl])
trait TransactionRepository {
  def create(data: TransactionData)(implicit mc: MarkerContext): Future[Option[Int]]

  def map()(implicit mc: MarkerContext): Future[Map[Int, TransactionData]]

  def get(id: Int)(implicit mc: MarkerContext): Future[Option[TransactionData]]
}


@Singleton
class TransactionRepositoryImpl @Inject()()(implicit ec: TransactionExecutionContext, implicit val ar: AccountRepository) extends TransactionRepository {

  private val logger = Logger(this.getClass)

  override def map()(implicit mc: MarkerContext): Future[Map[Int, TransactionData]] = {
    Future {
      logger.trace(s"list: ")
      Transactions.map()
    }
  }

  override def get(id: Int)(implicit mc: MarkerContext): Future[Option[TransactionData]] = {
    Future {
      logger.trace(s"get: id = $id")
      Transactions.get(id)
    }
  }

  def create(data: TransactionData)(implicit mc: MarkerContext): Future[Option[Int]] = {
    Future {
      logger.trace(s"create: data = $data")
      Transactions.create(data)
    }
  }
}
