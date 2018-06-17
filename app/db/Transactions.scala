package db

import scala.collection.mutable
import javax.inject.Inject
import play.api.{Configuration, Environment, Logger}
import play.api.libs.json._
import play.api.libs.functional.syntax._


final case class TransactionData(sourceAccount: Int, destinationAccount: Int, amount: Double)


case class RateResponse(success: Boolean, timestamp: Int, base:String, date:String, rates: Map[String, Double])

object RateResponse {

  implicit val implicitWrites: Writes[RateResponse] = new Writes[RateResponse] {
    def writes(rate: RateResponse): JsValue = {
      Json.obj(
        "success" -> rate.success,
        "timestamp" -> rate.timestamp,
        "base" -> rate.base,
        "date" -> rate.date,
        "rates" -> rate.rates
      )
    }
  }

  implicit val implicitReads: Reads[RateResponse] = (
    (JsPath \ "success").read[Boolean] and
      (JsPath \ "timestamp").read[Int] and
      (JsPath \ "base").read[String] and
      (JsPath \ "date").read[String] and
      (JsPath \ "rates").read[Map[String, Double]]
    )(RateResponse.apply _)
}

class AppConfig @Inject()(conf: Configuration){
  val accessKey: String = conf.get[String]("fixer.api_key")
}

object Transactions {

  private val log = Logger(this.getClass)

  private def getAccountId(id: Int): Int = {
    if (Accounts.checkId(id)) id else 0
  }

  private def getCoefficient(sourceIso: String, destIso: String): Double = {
    import scalaj.http._
    val conf = new AppConfig(Configuration.load(Environment.simple()))
    log.debug("conf.accessKey")
    log.debug(conf.accessKey)
    log.debug(sourceIso)
    log.debug(destIso)
    val response = Http("http://data.fixer.io/api/latest")
      .param("access_key", conf.accessKey)
      .param("symbols", s"$sourceIso,$destIso")
      .asString
    log.debug(response.body)
    val binResponse: RateResponse = Json.parse(response.body).as[RateResponse]
    log.debug(binResponse.toString)
    val sourceCoef = binResponse.rates(sourceIso)
    val destCoef = binResponse.rates(destIso)
    destCoef / sourceCoef
  }

  private def changeBalances(data: TransactionData): Boolean = {
    log.debug("Start change")
    if (data.sourceAccount != data.destinationAccount) {
      log.debug("not equal accounts in transaction")
      val sourceAcc = Accounts.get(data.sourceAccount)
      val destAcc = Accounts.get(data.destinationAccount)
      (sourceAcc, destAcc) match {
        case (Some(sa), Some(da)) => {
          log.debug("accounts exists")
          if (sa.balance - data.amount > 0) {
            log.debug("in source account enough money for transaction")
            val sourceCurrency = Currencies.get(sa.currencyId)
            val destinationCurrency = Currencies.get(da.currencyId)
            (sourceCurrency, destinationCurrency) match {
              case (Some(sc), Some(dc)) => {
                log.debug("currencies exists")
                // We need to get currency rate using API for realtime convertation
                val coefficient = getCoefficient(sc.iso2, dc.iso2)
                log.debug("Before transaction")
                log.debug(sa.toString)
                log.debug(da.toString)
                sa.balance -= data.amount
                da.balance += data.amount * coefficient
                log.debug("After transaction")
                log.debug(sa.toString)
                log.debug(da.toString)
                true
              }
              case _ => false
            }
          } else {
            false
          }
        }
        case _ => false
      }
    } else {
      false
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
