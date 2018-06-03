package api.accounts

import javax.inject.Inject
import play.api.MarkerContext

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._
import db.{AccountData, AccountId, CurrencyId, UserId}

/**
  * DTO for displaying account information.
  */
case class AccountResource(id: String,
                           user: String,
                           currency: String,
                           balance: Double)



object AccountResource {

  /**
    * Mapping to write a UserResource out as a JSON value.
    */
  implicit val implicitWrites = new Writes[AccountResource] {
    def writes(account: AccountResource): JsValue = {
      Json.obj(
        "id" -> account.id,
        "user" -> account.user,
        "currency" -> account.currency,
        "balance" -> account.balance
      )
    }
  }
}

/**
  * Controls access to the backend data, returning [[AccountResource]]
  */
class AccountResourceHandler @Inject()(
    //routerProvider: Provider[AccountsRouter],
    accountRepository: AccountRepository)(implicit ec: ExecutionContext) {

  // get single account
  def find(id: String)(
    implicit mc: MarkerContext): Future[Option[AccountResource]] = {
    println(id)
    val accountFuture = accountRepository.get(id)
    accountFuture.map { maybeAccountData =>
      maybeAccountData.map { accountData =>
        createAccountResource(accountData)
      }
    }
  }

  // get accounts list
  def takeList(implicit mc: MarkerContext): Future[Iterable[AccountResource]] = {
    accountRepository.list().map { accountDataList =>
      accountDataList.map(accountData => createAccountResource(accountData))
    }
  }

  // create new account
  def create(accountInput: AccountFormInput)(
    implicit mc: MarkerContext): Future[AccountId] = {
    val data = AccountData(AccountId(),
      UserId(accountInput.user_id),
      CurrencyId(accountInput.currency_id),
      accountInput.balance)
    accountRepository.create(data)
  }

  // update existing account
  def update(id: String, accountInput: AccountFormInput)(
    implicit mc: MarkerContext): Future[Boolean]= {
    val data = AccountData(AccountId(id),
      UserId(accountInput.user_id),
      CurrencyId(accountInput.currency_id),
      accountInput.balance)
    accountRepository.update(data)
  }

  // delete existing account
  def delete(id: String)(
    implicit mc: MarkerContext): Future[Boolean]= {
    accountRepository.delete(id)
  }

  private def createAccountResource(u: AccountData): AccountResource = {
    AccountResource(u.id.toString, u.userId.toString, u.currencyId.toString, u.balance)
  }
}
