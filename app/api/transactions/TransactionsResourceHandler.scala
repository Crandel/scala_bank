package api.transactions

import api.transactions.CurrencyId
import api.users.UserId
import javax.inject.{Inject, Provider}
import play.api.MarkerContext

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._

/**
  * DTO for displaying transaction information.
  */
case class TransactionResource(id: String,
                               source_user: String,
                               destination_user: String,
                               amount: Double)


object TransactionResource {

  /**
    * Mapping to write a UserResource out as a JSON value.
    */
  implicit val implicitWrites = new Writes[TransactionResource] {
    def writes(transaction: TransactionResource): JsValue = {
      Json.obj(
        "id" -> transaction.id,
        "source_user" -> transaction.source_user,
        "destination_user" -> transaction.destination_user,
        "amount" -> transaction.amount
      )
    }
  }
}

/**
  * Controls access to the backend data, returning [[TransactionResource]]
  */
class TransactionResourceHandler @Inject()(
    routerProvider: Provider[TransactionsRouter],
    transactionRepository: TransactionRepository,
    currencyRepository: CurrencyRepository)(implicit ec: ExecutionContext) {

  // get single transaction
  def find(id: String)(
    implicit mc: MarkerContext): Future[Option[TransactionResource]] = {
    val transactionFuture = transactionRepository.get(TransactionId(id))
    transactionFuture.map { maybeTransactionData =>
      maybeTransactionData.map { transactionData =>
        createTransactionResource(transactionData)
      }
    }
  }

  // get transactions list
  def takeList(implicit mc: MarkerContext): Future[Iterable[TransactionResource]] = {
    transactionRepository.list().map { transactionDataList =>
      transactionDataList.map(transactionData => createTransactionResource(transactionData))
    }
  }

  // create new transaction
  def create(transactionInput: TransactionFormInput)(
    implicit mc: MarkerContext): Future[TransactionId] = {
    val data = TransactionData(TransactionId(),
      UserId(transactionInput.user_id),
      CurrencyId(transactionInput.currency_id),
      0)
    // We don't actually create the post, so return what we have
    transactionRepository.create(data)
  }

  // update existing transaction
  def update(id: String, transactionInput: TransactionFormInput)(
    implicit mc: MarkerContext): Future[Boolean]= {
    val transactionIDObj = TransactionId(id)
    val data = TransactionData(transactionIDObj,
      UserId(transactionInput.user_id),
      CurrencyId(transactionInput.currency_id),
      0)
    // We don't actually create the post, so return what we have
    transactionRepository.update(transactionIDObj, data)
  }

  // delete existing transaction
  def delete(id: String)(
    implicit mc: MarkerContext): Future[Boolean]= {
    val transactionIdObj = TransactionId(id)
    // We don't actually create the post, so return what we have
    transactionRepository.delete(transactionIdObj)
  }

  private def createTransactionResource(trData: TransactionData): TransactionResource = {
    TransactionResource(trData.id.toString, trData.sourceAccount.toString, trData.destinationAccount.toString, trData.amount)
  }
}
