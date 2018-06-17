package api.transactions

import db.CurrencyData
import javax.inject.Inject
import play.api.MarkerContext

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._

/**
  * DTO for displaying currency information.
  */
case class CurrencyResource(id: Int,
                            name: String,
                            iso2: String)


object CurrencyResource {

  /**
    * Mapping to write a CurrencyResource out as a JSON value.
    */
  implicit val implicitWrites: Writes[CurrencyResource] = new Writes[CurrencyResource] {
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

  // get single currency
  def get(id: Int)(
    implicit mc: MarkerContext): Future[Option[CurrencyResource]] = {
    val currencyFuture = currencyRepository.get(id)
    currencyFuture.map { maybeCurrencyData =>
      maybeCurrencyData.map { currencyData =>
        createCurrencyResource(id, currencyData)
      }
    }
  }

  // get currency list
  def findAll(implicit mc: MarkerContext): Future[Iterable[CurrencyResource]] = {
    currencyRepository.map().map { currencyDataList =>
      currencyDataList.map(currencyData => createCurrencyResource(currencyData._1, currencyData._2))
    }
  }

  // create currency resource for response
  private def createCurrencyResource(id: Int, c: CurrencyData): CurrencyResource = {
    CurrencyResource(id, c.name, c.iso2)
  }
}
