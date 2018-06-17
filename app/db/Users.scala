package db

import play.api.Logger

import scala.collection.mutable

final case class UserData(name: String,
                          login: String,
                          password: String)


object Users {
  private val log = Logger(this.getClass)

  private def init() = {
    mutable.HashMap(
      1 -> UserData("name 1", "login 1", "password 1"),
      2 -> UserData("name 2", "login 2", "password 2"),
      3 -> UserData("name 3", "login 3", "password 3"),
      4 -> UserData("name 4", "login 4", "password 4"),
      5 -> UserData("name 5", "login 5", "password 5")
    )
  }
  private val usersMap = init()

  def map(): Map[Int, UserData] = {
    usersMap.toMap
  }

  def checkId(id: Int): Boolean = {
    usersMap.contains(id)
  }

  def get(id: Int): Option[UserData] = {
    if (checkId(id)) Some(usersMap(id)) else None
  }

  def create(data: UserData): Int = {
    val newKey = usersMap.keys.max + 1
    usersMap(newKey) = data
    newKey
  }

  def update(id: Int, data: UserData): Boolean = {
    if (checkId(id)){
      usersMap(id) = data
      true
    } else {
      false
    }
  }

  def delete(id: Int): Boolean = {
    log.info("Start delete")
    if (checkId(id)){
      log.info("id fine")
      log.info(usersMap.toString())
      usersMap -= id
      log.info(usersMap.toString())
      true
    } else {
      false
    }
  }
}
