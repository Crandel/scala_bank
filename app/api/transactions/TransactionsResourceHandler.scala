package api.transactions

import db.TransactionData
import javax.inject.{Inject, Provider}
import play.api.MarkerContext

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._

/**
  * DTO for displaying transaction information.
  */
case class TransactionResource(id: Int,
                               source_user: Int,
                               destination_user: Int,
                               amount: Double)


object TransactionResource {

  /**
    * Mapping to write a UserResource out as a JSON value.
    */
  implicit val implicitWrites: Writes[TransactionResource] = new Writes[TransactionResource] {
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
  def find(id: Int)(
    implicit mc: MarkerContext): Future[Option[TransactionResource]] = {
    val transactionFuture = transactionRepository.get(id)
    transactionFuture.map { maybeTransactionData =>
      maybeTransactionData.map { transactionData =>
        createTransactionResource(id, transactionData)
      }
    }
  }

  // get transactions list
  def takeList(implicit mc: MarkerContext): Future[Iterable[TransactionResource]] = {
    transactionRepository.map().map { transactionDataList =>
      transactionDataList.map(transactionData => createTransactionResource(transactionData._1, transactionData._2))
    }
  }

  // create new transaction
  def create(transactionInput: TransactionFormInput)(
    implicit mc: MarkerContext): Future[Option[Int]] = {
    val data = TransactionData(
      transactionInput.source_id,
      transactionInput.destination_id,
      transactionInput.amount)
    transactionRepository.create(data)
  }

  private def createTransactionResource(id: Int, trData: TransactionData): TransactionResource = {
    TransactionResource(id, trData.sourceAccount, trData.destinationAccount, trData.amount)
  }
}

