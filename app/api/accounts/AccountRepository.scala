package api.accounts

import akka.actor.ActorSystem
import javax.inject.Inject
import play.api.libs.concurrent.CustomExecutionContext
import play.api.{Logger, MarkerContext}

import scala.concurrent.Future

final case class AccountData(id: AccountId, userId: UserId, currencyId: CurrencyId, balance: Double)

class AccountId private (val underlying: Int) extends AnyVal {
  override def toString: String = underlying.toString
}

object AccountId {
  def apply(raw: String): AccountId = {
    require(raw != null)
    new AccountId(Integer.parseInt(raw))
  }
}

class AccountExecutionContext @Inject()(actorSystem: ActorSystem) extends CustomExecutionContext(actorSystem, "repository.dispatcher")
/**
  * A pure non-blocking interface for the PostRepository.
  */
trait AccountRepository {
  def create(data: AccountData)(implicit mc: MarkerContext): Future[AccountId]

  def list()(implicit mc: MarkerContext): Future[Iterable[AccountData]]

  def get(id: AccountId)(implicit mc: MarkerContext): Future[Option[AccountData]]
}


@Singleton
class AccountRepositoryImpl @Inject()()(implicit ec: AccountExecutionContext) extends AccountRepository {

  private val logger = Logger(this.getClass)

  private val accountList = List(
    AccountData(AccountId("1"), UserId("1"), CurrencyId("1"), 10.0),
    AccountData(AccountId("2"), UserId("2"), CurrencyId("2"), 10.0),
    AccountData(AccountId("3"), UserId("3"), CurrencyId("3"), 10.0),
    AccountData(AccountId("4"), UserId("4"), CurrencyId("4"), 10.0),
    AccountData(AccountId("5"), UserId("5"), CurrencyId("5"), 10.0)
  )

  override def list()(implicit mc: MarkerContext): Future[Iterable[AccountData]] = {
    Future {
      logger.trace(s"list: ")
      accountList
    }
  }

  override def get(id: AccountId)(implicit mc: MarkerContext): Future[Option[AccountData]] = {
    Future {
      logger.trace(s"get: id = $id")
      accountList.find(user => user.id == id)
    }
  }

  def create(data: AccountData)(implicit mc: MarkerContext): Future[AccountId] = {
    Future {
      logger.trace(s"create: data = $data")
      data.id
    }
  }
}
