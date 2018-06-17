package db


import scala.collection.mutable


final case class CurrencyData(name: String, iso2: String)

object Currencies {

  private def init() = {
    mutable.HashMap(
      1 -> CurrencyData("dollar", "USD"),
      2 -> CurrencyData("euro", "EUR"),
      3 -> CurrencyData("pound", "GBP"),
      4 -> CurrencyData("hrivna", "UAH"),
      5 -> CurrencyData("ruble", "RUB")
    )

  }

  private val currencyMap = init()

  def map(): Map[Int, CurrencyData] = {
    currencyMap.toMap
  }

  def checkId(id: Int): Boolean = {
    currencyMap.contains(id)
  }

  def get(id: Int): Option[CurrencyData] = {
    if (checkId(id)) Some(currencyMap(id)) else None
  }

  def create(data: CurrencyData): Int = {
    val newKey = currencyMap.keys.max + 1
    currencyMap(newKey) = data
    newKey
  }

  def update(id: Int, data: CurrencyData): Boolean = {
    if (checkId(id)){
      currencyMap(id) = data
      true
    } else {
      false
    }
  }

  def delete(id: Int): Boolean = {
    if (checkId(id)){
      currencyMap - id
      true
    } else {
      false
    }
  }
}
