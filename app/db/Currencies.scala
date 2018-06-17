package db


import scala.collection.mutable


final case class CurrencyData(name: String, iso2: String)

object Currencies {

  private def init() = {
    mutable.HashMap(
      1 -> CurrencyData("Dollar", "USD"),
      2 -> CurrencyData("Euro", "EUR"),
      3 -> CurrencyData("Pound", "GBP"),
      4 -> CurrencyData("Hrivna", "UAH"),
      5 -> CurrencyData("Ruble", "RUB")
    )
  }

  private val currencyMap = init()

  def checkId(id: Int): Boolean = {
    currencyMap.contains(id)
  }

  def map(): Map[Int, CurrencyData] = {
    currencyMap.toMap
  }

  def get(id: Int): Option[CurrencyData] = {
    if (checkId(id)) Some(currencyMap(id)) else None
  }
}
