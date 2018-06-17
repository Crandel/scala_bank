package db

import play.api.Logger

import scala.collection.mutable


final case class AccountData(
  userId: Int,
  currencyId: Int,
  var balance: Double
)

object Accounts {

  private val log = Logger(this.getClass)

  private def getUserId(id: Int): Int = {
    if (Users.checkId(id)) id else 0
  }

  private def getCurrencyId(id: Int): Int = {
    if (Currencies.checkId(id)) id else 0
  }

  private def init() = {
    val currency1: Int = getCurrencyId(1)
    val currency2: Int = getCurrencyId(2)
    val currency3: Int = getCurrencyId(3)

    val user1: Int = getUserId(1)
    val user2: Int = getUserId(2)
    val user3: Int = getUserId(3)
    val user4: Int = getUserId(4)
    val user5: Int = getUserId(5)

    mutable.HashMap(
      1 -> AccountData(user1, currency1, 50.0),
      2 -> AccountData(user2, currency2, 30.0),
      3 -> AccountData(user3, currency1, 10.0),
      4 -> AccountData(user4, currency2, 10.0),
      5 -> AccountData(user5, currency3, 10.0)
    )

  }

  private val accountMap = init()

  def checkId(id: Int): Boolean = {
    accountMap.contains(id)
  }

  def get(id: Int): Option[AccountData] = {
    if (checkId(id)) Some(accountMap(id)) else None
  }

  def map(): Map[Int, AccountData] = accountMap.toMap

  def create(data: AccountData): Option[Int] = {
    val newKey = accountMap.keys.max + 1
    Users.get(data.userId) match {
      case Some(user) => {
        accountMap(newKey) = data
        Some(newKey)
      }
      case _ => None
    }
  }

  def update(id: Int, data: AccountData): Boolean = {
    if (checkId(id)){
      accountMap(id) = data
      true
    } else {
      false
    }
  }

  def delete(id: Int): Boolean = {
    log.info("Start delete")
    if (checkId(id)){
      log.info("id fine")
      log.info(accountMap.toString())
      accountMap -= id
      log.info(accountMap.toString())
      true
    } else {
      false
    }
  }
}
