package api.transactions

import scala.concurrent.Future
import scala.concurrent.duration._

import akka.actor.ActorSystem
import javax.inject.{Inject, Singleton}
import play.api.libs.concurrent.CustomExecutionContext
import play.api.{Logger, MarkerContext}

import api.accounts.{AccountId, AccountRepository}
import play.api.libs.json.{Json, Writes}


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
trait TransactionRepository {
  def create(data: TransactionData)(implicit mc: MarkerContext): Future[TransactionId]

  def list()(implicit mc: MarkerContext): Future[Iterable[TransactionData]]

  def get(id: TransactionId)(implicit mc: MarkerContext): Future[Option[TransactionData]]
}


@Singleton
class TransactionRepositoryImpl @Inject()()(implicit ec: TransactionExecutionContext, implicit val ar: AccountRepository) extends TransactionRepository {

  private val logger = Logger(this.getClass)

  private val accountFuture1 = ar.get(AccountId("1"))
  private val accountFuture2 = ar.get(AccountId("1"))
  private val account1: AccountId = accountFuture1.result(10.seconds) match {
    case Some(accountData) => accountData.id
  }

  private val account2: AccountId = accountFuture2.result(10.seconds) match {
    case Some(accountData) => accountData.id
  }

  private val transactionList = List(
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
}
