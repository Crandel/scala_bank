package db

import play.api.libs.json.{Json, Writes}

import scala.collection.mutable


final case class CurrencyData(id: CurrencyId, name: String, iso2: String)

class CurrencyId private (val underlying: Int) extends AnyVal {
  override def toString: String = underlying.toString
}

object CurrencyId {
  private var counter: Int = 0
  private var currentId: Int = 0

  implicit val currencyWrites = new Writes[CurrencyId] {
    def writes(currency: CurrencyId) = Json.obj(
      "id" -> currency.toString
    )
  }

  def apply(raw: String = ""): CurrencyId = {
    if (raw == "" ){
      currentId = counter
      counter += 1
    } else {
      currentId = Integer.parseInt(raw)
    }
    new CurrencyId(currentId)
  }
}

object Currencies {

  private def init() = {
    mutable.MutableList(
      CurrencyData(CurrencyId(), "dollar", "USD"),
      CurrencyData(CurrencyId(), "euro", "EUR"),
      CurrencyData(CurrencyId(), "hrivna", "UAH"),
      CurrencyData(CurrencyId(), "ruble", "RUB"),
      CurrencyData(CurrencyId(), "pound", "GBP")
    )

  }

  private var currencyList = init()

  def list(): mutable.MutableList[CurrencyData] = {
    currencyList
  }

  def get(id: String): Option[CurrencyData] = {
    currencyList.find(currency => currency.id == CurrencyId(id))
  }

  def create(data: CurrencyData): CurrencyId = {
    currencyList += data
    data.id
  }

  def update(data: CurrencyData): Boolean = {
    val currentCurrency = currencyList.filter(currency => currency.id == data.id)
    val result = if (currentCurrency.isEmpty){
      false
    } else {
      currencyList = currencyList.filter(currency => currency.id != data.id)
      currencyList += data
      true
    }
    result
  }

  def delete(id: String): Boolean = {
    val uId = CurrencyId(id)
    val currentCurrency = currencyList.filter(currency => currency.id == uId)
    val result = if (currentCurrency.isEmpty){
      false
    } else {
      currencyList = currencyList.filter(currency => currency.id != uId)
      true
    }
    result
  }
}
