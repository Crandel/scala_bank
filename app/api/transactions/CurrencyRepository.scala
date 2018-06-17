package api.transactions

import scala.concurrent.Future

import akka.actor.ActorSystem
import com.google.inject.ImplementedBy
import javax.inject.{Inject, Singleton}
import play.api.libs.concurrent.CustomExecutionContext
import play.api.{Logger, MarkerContext}

import db.{Currencies, CurrencyData}


class CurrencyExecutionContext @Inject()(actorSystem: ActorSystem) extends CustomExecutionContext(actorSystem, "repository.dispatcher")
/**
  * A pure non-blocking interface for the PostRepository.
  */
@ImplementedBy(classOf[CurrencyRepositoryImpl])
trait CurrencyRepository {
  def map()(implicit mc: MarkerContext): Future[Map[Int, CurrencyData]]

  def get(id: Int)(implicit mc: MarkerContext): Future[Option[CurrencyData]]
}


@Singleton
class CurrencyRepositoryImpl @Inject()()(implicit ec: CurrencyExecutionContext) extends CurrencyRepository {

  private val logger = Logger(this.getClass)

  override def map()(implicit mc: MarkerContext): Future[Map[Int, CurrencyData]] = {
    Future {
      logger.info(s"list: ")
      Currencies.map()
    }
  }

  override def get(id: Int)(implicit mc: MarkerContext): Future[Option[CurrencyData]] = {
    Future {
      logger.trace(s"get: id = $id")
      Currencies.get(id)
    }
  }
}
