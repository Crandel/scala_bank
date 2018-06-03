package db

import play.api.libs.json.{JsObject, Json, Writes}

import scala.collection.mutable

final case class UserData(id: UserId,
                          name: String,
                          login: String,
                          password: String)


class UserId private (val underlying: Int) extends AnyVal {
  override def toString: String = underlying.toString
}

object UserId {
  private var counter: Int = 0
  private var current_id: Int = 0

  implicit val userWrites: Writes[UserId] = new Writes[UserId] {
    def writes(user: UserId): JsObject = Json.obj(
      "id" -> user.toString
    )
  }

  def apply(raw: String = ""): UserId = {
    if (raw == "" ){
      current_id = counter
      counter += 1
    } else {
      current_id = Integer.parseInt(raw)
    }
    new UserId(current_id)
  }
}


object Users {

  private def init() = {
    mutable.MutableList(
      UserData(UserId(), "name 1", "login 1", "password 1"),
      UserData(UserId(), "name 2", "login 2", "password 2"),
      UserData(UserId(), "name 3", "login 3", "password 3"),
      UserData(UserId(), "name 4", "login 4", "password 4"),
      UserData(UserId(), "name 5", "login 5", "password 5")
    )
  }
  private var userList = init()

  def list(): mutable.MutableList[UserData] = {
    userList
  }

  def get(id: String): Option[UserData] = {
    userList.find(user => user.id == UserId(id))
  }

  def create(data: UserData): UserId = {
    userList += data
    data.id
  }

  def update(data: UserData): Boolean = {
    val currentUser = userList.filter(user => user.id == data.id)
    val result = if (currentUser.isEmpty){
      false
    } else {
      userList = userList.filter(user => user.id != data.id)
      userList += data
      true
    }
    result
  }

  def delete(id: String): Boolean = {
    val uId = UserId(id)
    val currentUser = userList.filter(user => user.id == uId)
    val result = if (currentUser.isEmpty){
      false
    } else {
      userList = userList.filter(user => user.id != uId)
      true
    }
    result
  }
}
