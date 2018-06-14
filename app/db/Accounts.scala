package db

import play.api.libs.json.{JsObject, Json, Writes}

import scala.collection.mutable


final case class AccountData(id: AccountId,
                             userId: UserId,
                             currencyId: CurrencyId,
                             balance: Double)

class AccountId private (val underlying: Int) extends AnyVal {
  override def toString: String = underlying.toString
}

object AccountId {
  private var currentId: Int = 0

  implicit val accountWrites: Writes[AccountId] = new Writes[AccountId] {
    def writes(account: AccountId): JsObject = Json.obj(
      "id" -> account.toString
    )
  }

  def apply(raw: String = ""): AccountId = {
    var counter = currentId
    if (raw == "" ){
      currentId += 1
    } else {
      counter = Integer.parseInt(raw)
    }
    new AccountId(counter)
  }
}


object Accounts {

  private def getUserId(id: String): UserId = {
    val uData = Users.get(id)
    uData match {
      case Some(ud) => ud.id
    }
  }

  private def getCurrencyId(id: String): CurrencyId = {
    val uData = Currencies.get(id)
    uData match {
      case Some(ud) => ud.id
    }
  }

  private def init() = {
    val currency1 = getCurrencyId("0")
    val currency2 = getCurrencyId("1")

    val user1: UserId = getUserId("1")
    val user2: UserId = getUserId("2")
    val user3: UserId = getUserId("3")
    val user4: UserId = getUserId("4")
    val user5: UserId = getUserId("5")

    mutable.MutableList(
      AccountData(AccountId(), user1, currency1, 50.0),
      AccountData(AccountId(), user2, currency2, 30.0),
      AccountData(AccountId(), user3, currency1, 10.0),
      AccountData(AccountId(), user4, currency1, 10.0),
      AccountData(AccountId(), user5, currency1, 10.0)
    )

  }

  private var accountList = init()

  def get(id: String): Option[AccountData] = {
    accountList.find(account => account.id == AccountId(id))
  }

  def list(): mutable.MutableList[AccountData] = accountList

  def create(data: AccountData): AccountId = {
    accountList += data
    data.id
  }


  def update(data: AccountData): Boolean = {
    val currentUser = accountList.filter(account => account.id == data.id)
    val result = if (currentUser.isEmpty){
      false
    } else {
      accountList = accountList.filter(account => account.id != data.id)
      accountList += data
      true
    }
    result
  }

  def delete(id: String): Boolean = {
    val uId = AccountId(id)
    val currentAccount = accountList.filter(account => account.id == uId)
    val result = if (currentAccount.isEmpty){
      false
    } else {
      accountList = accountList.filter(account => account.id != uId)
      true
    }
    result
  }
}
