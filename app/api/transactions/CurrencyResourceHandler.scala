package api.transactions

import javax.inject.{Inject, Provider}

import play.api.MarkerContext

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._

/**
  * DTO for displaying currency information.
  */
case class CurrencyResource(id: String,
                            name: String,
                            iso2: String)


object CurrencyResource {

  /**
    * Mapping to write a CurrencyResource out as a JSON value.
    */
  implicit val implicitWrites = new Writes[CurrencyResource] {
    def writes(currency: CurrencyResource): JsValue = {
      Json.obj(
        "id" -> currency.id,
        "name" -> currency.name,
        "iso2" -> currency.iso2
      )
    }
  }

}
/**
  * Controls access to the backend data, returning [[CurrencyResource]]
  */
class CurrencyResourceHandler @Inject()(currencyRepository: CurrencyRepository)
                                       (implicit ec: ExecutionContext) {

  // get currency list
  def lookup(id: String)(
    implicit mc: MarkerContext): Future[Option[CurrencyResource]] = {
    val currencyFuture = currencyRepository.get(CurrencyId(id))
    currencyFuture.map { maybeCurrencyData =>
      maybeCurrencyData.map { currencyData =>
        createCurrencyResource(currencyData)
      }
    }
  }

  // get single currency
  def find(implicit mc: MarkerContext): Future[Iterable[CurrencyResource]] = {
    currencyRepository.list().map { currencyDataList =>
      currencyDataList.map(currencyData => createCurrencyResource(currencyData))
    }
  }

  private def createCurrencyResource(c: CurrencyData): CurrencyResource = {
    CurrencyResource(c.id.toString, c.name, c.iso2)
  }
}
