package api.accounts

import scala.concurrent.Future
import scala.collection.mutable

import akka.actor.ActorSystem
import javax.inject.{Inject,Singleton}
import com.google.inject.ImplementedBy
import play.api.libs.concurrent.CustomExecutionContext
import play.api.{Logger, MarkerContext}
import play.api.libs.json._

import api.transactions.CurrencyId
import api.users.UserId

final case class AccountData(
  id: AccountId,
  userId: UserId,
  currencyId: CurrencyId,
  balance: Double
)

class AccountId private (val underlying: Int) extends AnyVal {
  override def toString: String = underlying.toString
}

object AccountId {
  private var counter: Int = 0
  private var currentId: Int = 0

  implicit val userWrites = new Writes[AccountId] {
    def writes(account: AccountId) = Json.obj(
      "id" -> account.toString
    )
  }

  def apply(raw: String = ""): AccountId = {
    if (raw == "" ){
      currentId = counter
      counter += 1
    } else {
      currentId = Integer.parseInt(raw)
    }
    new AccountId(currentId)
  }
}

class AccountExecutionContext @Inject()(actorSystem: ActorSystem) extends CustomExecutionContext(actorSystem, "repository.dispatcher")

@ImplementedBy(classOf[AccountRepositoryImpl])
trait AccountRepository {
  def list()(implicit mc: MarkerContext): Future[Iterable[AccountData]]

  def get(id: AccountId)(implicit mc: MarkerContext): Future[Option[AccountData]]

  def create(data: AccountData)(implicit mc: MarkerContext): Future[AccountId]

  def update(id: AccountId, data: AccountData)(implicit mc: MarkerContext): Future[Boolean]

  def delete(id: AccountId)(implicit mc: MarkerContext): Future[Boolean]
}


@Singleton
class AccountRepositoryImpl @Inject()()(implicit ec: AccountExecutionContext) extends AccountRepository {

  private val logger = Logger(this.getClass)

  private var accountList = mutable.MutableList(
    AccountData(AccountId(), UserId(), CurrencyId("1"), 10.0),
    AccountData(AccountId(), UserId(), CurrencyId("2"), 10.0),
    AccountData(AccountId(), UserId(), CurrencyId("3"), 10.0),
    AccountData(AccountId(), UserId(), CurrencyId("4"), 10.0),
    AccountData(AccountId(), UserId(), CurrencyId("5"), 10.0)
  )

  override def list()(implicit mc: MarkerContext): Future[Iterable[AccountData]] = {
    Future {
      logger.info(s"list: ")
      accountList
    }
  }

  override def get(id: AccountId)(implicit mc: MarkerContext): Future[Option[AccountData]] = {
    Future {
      logger.info(s"get: id = $id")
      accountList.find(user => user.id == id)
    }
  }

  def create(data: AccountData)(implicit mc: MarkerContext): Future[AccountId] = {
    Future {
      logger.info(s"create: data = $data")
      data.id
    }
  }

  def update(id: AccountId, data: AccountData)(implicit mc: MarkerContext): Future[Boolean] = {
    Future {
      logger.info(s"update: data = $data")
      val currentAccount = accountList.filter(account => account.id == id)
      val result = if (currentAccount.isEmpty){
        false
      } else {
        accountList = accountList.filter(account => account.id != id)
        accountList += data
        true
      }
      result
    }
  }

  def delete(id: AccountId)(implicit mc: MarkerContext): Future[Boolean] = {
    Future {
      logger.info(s"delete: ")
      val currentAccount = accountList.filter(account => account.id == id)
      val result = if (currentAccount.isEmpty){
        false
      } else {
        accountList = accountList.filter(account => account.id != id)
        true
      }
      result
    }
  }
}
