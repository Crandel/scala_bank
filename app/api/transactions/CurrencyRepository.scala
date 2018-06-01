package api.transactions

import akka.actor.ActorSystem
import javax.inject.{Inject,Singleton}
import play.api.libs.concurrent.CustomExecutionContext
import play.api.{Logger, MarkerContext}

import scala.concurrent.Future

final case class CurrencyData(id: CurrencyId, name: String, iso2: String)

class CurrencyId private (val underlying: Int) extends AnyVal {
  override def toString: String = underlying.toString
}

object CurrencyId {
  def apply(raw: String): CurrencyId = {
    require(raw != null)
    new CurrencyId(Integer.parseInt(raw))
  }
}

class CurrencyExecutionContext @Inject()(actorSystem: ActorSystem) extends CustomExecutionContext(actorSystem, "repository.dispatcher")
/**
  * A pure non-blocking interface for the PostRepository.
  */
trait CurrencyRepository {
  def list()(implicit mc: MarkerContext): Future[Iterable[CurrencyData]]

  def get(id: CurrencyId)(implicit mc: MarkerContext): Future[Option[CurrencyData]]
}


@Singleton
class CurrencyRepositoryImpl @Inject()()(implicit ec: CurrencyExecutionContext) extends CurrencyRepository {

  private val logger = Logger(this.getClass)

  private val currencyList = List(
    CurrencyData(CurrencyId("1"), "dollar", "USD"),
    CurrencyData(CurrencyId("2"), "euro", "EUR"),
    CurrencyData(CurrencyId("3"), "hrivna", "UAH"),
    CurrencyData(CurrencyId("4"), "ruble", "RUB"),
    CurrencyData(CurrencyId("5"), "pound", "GBP")
  )

  override def list()(implicit mc: MarkerContext): Future[Iterable[CurrencyData]] = {
    Future {
      logger.info(s"list: ")
      currencyList
    }
  }

  override def get(id: CurrencyId)(implicit mc: MarkerContext): Future[Option[CurrencyData]] = {
    Future {
      logger.trace(s"get: id = $id")
      currencyList.find(currency => currency.id == id)
    }
  }
}
