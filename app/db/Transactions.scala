package db

import play.api.libs.json.{JsObject, Json, Writes}

import scala.collection.mutable

final case class TransactionData(id: TransactionId, sourceAccount: AccountId, destinationAccount: AccountId, amount: Double)

class TransactionId private (val underlying: Int) extends AnyVal {
  override def toString: String = underlying.toString
}

object TransactionId {
  private var currentId: Int = 0

  implicit val transactionIdWrites: Writes[TransactionId] = new Writes[TransactionId] {
    def writes(transaction: TransactionId): JsObject = Json.obj(
      "id" -> transaction.toString
    )
  }

  def apply(raw: String = ""): TransactionId = {
    var counter = currentId
    if (raw == "" ){
      currentId += 1
    } else {
      counter = Integer.parseInt(raw)
    }
    new TransactionId(counter)
  }
}

object Transactions {
  private def getAccountId(id: String): AccountId = {
    val uData = Accounts.get(id)
    uData match {
      case Some(ud) => ud.id
    }
  }

  private def init() = {
    val account1 = getAccountId("1")
    val account2 = getAccountId("2")

    mutable.MutableList(
      TransactionData(TransactionId(), account1, account2, 5.0),
      TransactionData(TransactionId(), account1, account2, 2.0),
      TransactionData(TransactionId(), account2, account1, 1.0),
      TransactionData(TransactionId(), account2, account1, 8.0),
      TransactionData(TransactionId(), account1, account2, 4.0)
    )
  }

  private var transactionList = init()

  def list(): mutable.MutableList[TransactionData] = {
    transactionList
  }

  def get(id: String): Option[TransactionData] = {
    transactionList.find(transaction => transaction.id == TransactionId(id))
  }

  def create(data: TransactionData): TransactionId = {
    transactionList += data
    data.id
  }

  def update(data: TransactionData): Boolean = {
    val currentTransaction = transactionList.filter(transaction => transaction.id == data.id)
    val result = if (currentTransaction.isEmpty){
      false
    } else {
      transactionList = transactionList.filter(transaction => transaction.id != data.id)
      transactionList += data
      true
    }
    result
  }

  def delete(id: String): Boolean = {
    val uId = TransactionId(id)
    val currentTransaction = transactionList.filter(transaction => transaction.id == uId)
    val result = if (currentTransaction.isEmpty){
      false
    } else {
      transactionList = transactionList.filter(transaction => transaction.id != uId)
      true
    }
    result
  }
}
