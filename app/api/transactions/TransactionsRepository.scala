package api.transactions

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.util.{Success, Try}
import scala.collection.mutable
import akka.actor.ActorSystem
import javax.inject.{Inject, Singleton}
import play.api.libs.concurrent.CustomExecutionContext
import play.api.{Logger, MarkerContext}
import play.api.libs.json.{Json, Writes}
import api.accounts.{AccountData, AccountId, AccountRepository}
import com.google.inject.ImplementedBy


final case class TransactionData(id: TransactionId, sourceAccount: AccountId, destinationAccount: AccountId, amount: Double)

class TransactionId private (val underlying: Int) extends AnyVal {
  override def toString: String = underlying.toString
}

object TransactionId {
  private var counter: Int = 0
  private var currentId: Int = 0

  implicit val transactionIdWrites = new Writes[TransactionId] {
    def writes(transaction: TransactionId) = Json.obj(
      "id" -> transaction.toString
    )
  }

  def apply(raw: String = ""): TransactionId = {
    if (raw == "" ){
      currentId = counter
      counter += 1
    } else {
      currentId = Integer.parseInt(raw)
    }
    new TransactionId(currentId)
  }
}

class TransactionExecutionContext @Inject()(actorSystem: ActorSystem) extends CustomExecutionContext(actorSystem, "repository.dispatcher")
/**
  * A pure non-blocking interface for the PostRepository.
  */
@ImplementedBy(classOf[TransactionRepositoryImpl])
trait TransactionRepository {
  def create(data: TransactionData)(implicit mc: MarkerContext): Future[TransactionId]

  def list()(implicit mc: MarkerContext): Future[Iterable[TransactionData]]

  def get(id: TransactionId)(implicit mc: MarkerContext): Future[Option[TransactionData]]

  def update(id: TransactionId, data: TransactionData)(implicit mc: MarkerContext): Future[Boolean]

  def delete(id: TransactionId)(implicit mc: MarkerContext): Future[Boolean]

}


@Singleton
class TransactionRepositoryImpl @Inject()()(implicit ec: TransactionExecutionContext, implicit val ar: AccountRepository) extends TransactionRepository {

  private val logger = Logger(this.getClass)

  private def getAccountData(id: String) = {
    Try(Await.result(ar.get(AccountId(id)), 10.seconds)) match {
      case Success(accountDataOpt: Option[AccountData]) => accountDataOpt match {
        case Some(accountData) => accountData.id
        case None => AccountId(id)
      }
    }
  }

  private val account1 = getAccountData("1")
  private val account2 = getAccountData("2")

  private var transactionList = mutable.MutableList(
    TransactionData(TransactionId(), account1, account2, 5.0),
    TransactionData(TransactionId(), account1, account2, 2.0),
    TransactionData(TransactionId(), account2, account1, 1.0),
    TransactionData(TransactionId(), account2, account1, 8.0),
    TransactionData(TransactionId(), account1, account2, 4.0)
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

  def update(id: TransactionId, data: TransactionData)(implicit mc: MarkerContext): Future[Boolean] = {
    Future {
      logger.info(s"update: data = $data")
      val currentTransaction = transactionList.filter(transaction => transaction.id == id)
      val result = if (currentTransaction.isEmpty){
        false
      } else {
        transactionList = transactionList.filter(transaction => transaction.id != id)
        transactionList += data
        true
      }
      result
    }
  }

  def delete(id: TransactionId)(implicit mc: MarkerContext): Future[Boolean] = {
    Future {
      logger.info(s"delete: ")
      val currentTransaction = transactionList.filter(transaction => transaction.id == id)
      val result = if (currentTransaction.isEmpty){
        false
      } else {
        transactionList = transactionList.filter(transaction => transaction.id != id)
        true
      }
      result
    }
  }

}
