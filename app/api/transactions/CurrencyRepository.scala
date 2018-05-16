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
  def create(data: CurrencyData)(implicit mc: MarkerContext): Future[CurrencyId]

  def list()(implicit mc: MarkerContext): Future[Iterable[CurrencyData]]

  def get(id: CurrencyId)(implicit mc: MarkerContext): Future[Option[CurrencyData]]
}


@Singleton
class CurrencyRepositoryImpl @Inject()()(implicit ec: CurrencyExecutionContext) extends CurrencyRepository {

  private val logger = Logger(this.getClass)

  private val currencyList = List(
    CurrencyData(CurrencyId("1"), "name 1", "iso2 1"),
    CurrencyData(CurrencyId("2"), "name 2", "iso2 2"),
    CurrencyData(CurrencyId("3"), "name 3", "iso2 3"),
    CurrencyData(CurrencyId("4"), "name 4", "iso2 4"),
    CurrencyData(CurrencyId("5"), "name 5", "iso2 5")
  )

  override def list()(implicit mc: MarkerContext): Future[Iterable[CurrencyData]] = {
    Future {
      logger.trace(s"list: ")
      currencyList
    }
  }

  override def get(id: CurrencyId)(implicit mc: MarkerContext): Future[Option[CurrencyData]] = {
    Future {
      logger.trace(s"get: id = $id")
      currencyList.find(currency => currency.id == id)
    }
  }

  def create(data: CurrencyData)(implicit mc: MarkerContext): Future[CurrencyId] = {
    Future {
      logger.trace(s"create: data = $data")
      data.id
    }
  }

}
