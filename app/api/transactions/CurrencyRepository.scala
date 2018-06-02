package api.transactions

import akka.actor.ActorSystem
import com.google.inject.ImplementedBy
import javax.inject.{Inject, Singleton}
import play.api.libs.concurrent.CustomExecutionContext
import play.api.libs.json.{Json, Writes}
import play.api.{Logger, MarkerContext}

import scala.concurrent.Future

final case class CurrencyData(id: CurrencyId, name: String, iso2: String)

class CurrencyId private (val underlying: Int) extends AnyVal {
  override def toString: String = underlying.toString
}

object CurrencyId {
  private var counter: Int = 0
  private var currentId: Int = 0

  implicit val userWrites = new Writes[CurrencyId] {
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

class CurrencyExecutionContext @Inject()(actorSystem: ActorSystem) extends CustomExecutionContext(actorSystem, "repository.dispatcher")
/**
  * A pure non-blocking interface for the PostRepository.
  */
@ImplementedBy(classOf[CurrencyRepositoryImpl])
trait CurrencyRepository {
  def list()(implicit mc: MarkerContext): Future[Iterable[CurrencyData]]

  def get(id: CurrencyId)(implicit mc: MarkerContext): Future[Option[CurrencyData]]
}


@Singleton
class CurrencyRepositoryImpl @Inject()()(implicit ec: CurrencyExecutionContext) extends CurrencyRepository {

  private val logger = Logger(this.getClass)

  private val currencyList = List(
    CurrencyData(CurrencyId(), "dollar", "USD"),
    CurrencyData(CurrencyId(), "euro", "EUR"),
    CurrencyData(CurrencyId(), "hrivna", "UAH"),
    CurrencyData(CurrencyId(), "ruble", "RUB"),
    CurrencyData(CurrencyId(), "pound", "GBP")
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
