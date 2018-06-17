package api.accounts

import javax.inject.{Inject, Provider}
import play.api.MarkerContext

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._
import db.AccountData

/**
  * DTO for displaying account information.
  */
case class AccountResource(id: Int,
                           user: Int,
                           currency: Int,
                           balance: Double)



object AccountResource {

  /**
    * Mapping to write a UserResource out as a JSON value.
    */
  implicit val implicitWrites: Writes[AccountResource] = new Writes[AccountResource] {
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
    routerProvider: Provider[AccountsRouter],
    accountRepository: AccountRepository)(implicit ec: ExecutionContext) {

  // get single account
  def find(id: Int)(
    implicit mc: MarkerContext): Future[Option[AccountResource]] = {
    val accountFuture = accountRepository.get(id)
    accountFuture.map { maybeAccountData =>
      maybeAccountData.map { accountData =>
        createAccountResource(id, accountData)
      }
    }
  }

  // get accounts list
  def findAll(implicit mc: MarkerContext): Future[Iterable[AccountResource]] = {
    accountRepository.map().map { accountDataList =>
      accountDataList.map(accountData => createAccountResource(accountData._1, accountData._2))
    }
  }

  // create new account
  def create(accountInput: AccountFormInput)(
    implicit mc: MarkerContext): Future[Option[Int]] = {
    val data = AccountData(
      accountInput.user_id,
      accountInput.currency_id,
      accountInput.balance)
    accountRepository.create(data)
  }

  // update existing account
  def update(id: Int, accountInput: AccountFormInput)(
    implicit mc: MarkerContext): Future[Boolean]= {
    val data = AccountData(
      accountInput.user_id,
      accountInput.currency_id,
      accountInput.balance)
    accountRepository.update(id, data)
  }

  // delete existing account
  def delete(id: Int)(
    implicit mc: MarkerContext): Future[Boolean]= {
    accountRepository.delete(id)
  }

  private def createAccountResource(id: Int, u: AccountData): AccountResource = {
    AccountResource(id, u.userId, u.currencyId, u.balance)
  }
}
