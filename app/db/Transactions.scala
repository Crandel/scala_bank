package db

import com.typesafe.config.Config
import javax.inject.Inject

import scala.collection.mutable

final case class TransactionData(sourceAccount: Int, destinationAccount: Int, amount: Double)

object Transactions {

  @Inject() val conf: Config = null

  private def getAccountId(id: Int): Int = {
    if (Accounts.checkId(id)) id else 0
  }

  private def getCoefficient(sourceIso: String, destIso: String) = {
    //import scalaj.http._
    //val response: HttpResponse[Map[String,String]] = Http(s"http://data.fixer.io/api/latest?access_key=${conf.fixer.api_key}").execute(parser = {inputStream =>
    //  Json.parse[Map[String,String]](inputStream)
    //})
  }

  private def changeBalances(data: TransactionData): Boolean = {
    val sourceAcc = Accounts.get(data.sourceAccount)
    val destAcc = Accounts.get(data.destinationAccount)
    (sourceAcc, destAcc) match {
      case (Some(sa), Some(da)) => {
        if (sa.balance - data.amount > 0) {
          //val sourceCurrency = Currencies.get(sa.currencyId)
          //val destinationCurrency = Currencies.get(da.currencyId)

          sa.balance -= data.amount
          da.balance += data.amount
          true
        } else {
          false
        }
      }
      case _ => false
    }
  }

  private def init() = {
    val account1 = getAccountId(1)
    val account2 = getAccountId(2)

    mutable.HashMap(
      1 -> TransactionData(account1, account2, 5.0),
      2 -> TransactionData(account1, account2, 2.0),
      3 -> TransactionData(account2, account1, 1.0),
      4 -> TransactionData(account2, account1, 8.0),
      5 -> TransactionData(account1, account2, 4.0)
    )
  }

  private val transactionMap = init()

  def checkId(id: Int): Boolean = {
    transactionMap.contains(id)
  }

  def map(): Map[Int, TransactionData] = {
    transactionMap.toMap
  }

  def get(id: Int): Option[TransactionData] = {
    if (checkId(id)) Some(transactionMap(id)) else None
  }

  def create(data: TransactionData): Option[Int] = {
    val newKey = transactionMap.keys.max + 1
    if (changeBalances(data)) {
      transactionMap(newKey) = data
      Some(newKey)
    } else {
      None
    }
  }
}
