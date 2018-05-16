package api.transactions

import scala.concurrent.Future

import akka.actor.ActorSystem
import javax.inject.{Inject,Singleton}
import play.api.libs.concurrent.CustomExecutionContext
import play.api.{Logger, MarkerContext}

import api.users.UserId


final case class TransactionData(id: TransactionId, sourceAccount: UserId, destinationAccount: UserId, amount: Double)

class TransactionId private (val underlying: Int) extends AnyVal {
  override def toString: String = underlying.toString
}

object TransactionId {
  def apply(raw: String): TransactionId = {
    require(raw != null)
    new TransactionId(Integer.parseInt(raw))
  }
}

class TransactionExecutionContext @Inject()(actorSystem: ActorSystem) extends CustomExecutionContext(actorSystem, "repository.dispatcher")
/**
  * A pure non-blocking interface for the PostRepository.
  */
trait TransactionRepository {
  def create(data: TransactionData)(implicit mc: MarkerContext): Future[TransactionId]

  def list()(implicit mc: MarkerContext): Future[Iterable[TransactionData]]

  def get(id: TransactionId)(implicit mc: MarkerContext): Future[Option[TransactionData]]
}


@Singleton
class TransactionRepositoryImpl @Inject()()(implicit ec: TransactionExecutionContext) extends TransactionRepository {

  private val logger = Logger(this.getClass)

  private val transactionList = List(
    TransactionData(TransactionId("1"), UserId("1"), UserId("2"), 5.0),
    TransactionData(TransactionId("2"), UserId("2"), UserId("1"), 5.0),
    TransactionData(TransactionId("3"), UserId("2"), UserId("3"), 5.0),
    TransactionData(TransactionId("4"), UserId("3"), UserId("4"), 5.0),
    TransactionData(TransactionId("5"), UserId("4"), UserId("5"), 5.0)
  )

  override def list()(implicit mc: MarkerContext): Future[Iterable[TransactionData]] = {
    Future {
      logger.trace(s"list: ")
      transactionList
    }
  }

  override def get(id: TransactionId)(implicit mc: MarkerContext): Future[Option[TransactionData]] = {
    Future {
      logger.trace(s"get: id = $id")
      transactionList.find(user => user.id == id)
    }
  }

  def create(data: TransactionData)(implicit mc: MarkerContext): Future[TransactionId] = {
    Future {
      logger.trace(s"create: data = $data")
      data.id
    }
  }
}
