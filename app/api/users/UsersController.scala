package api.users

import javax.inject.Inject
import play.api.Logger
import play.api.data.Form
import play.api.libs.json.{JsObject, Json, Writes}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

case class UserFormInput(name: String, login: String, password: String)

class UsersController @Inject()(cc: UserControllerComponents)(
  implicit ec: ExecutionContext)
  extends UserBaseController(cc) {

  private val logger = Logger(getClass)

  private val form: Form[UserFormInput] = {
    import play.api.data.Forms._

    Form(
      mapping(
        "name" -> nonEmptyText,
        "login" -> text,
        "password" -> nonEmptyText
      )(UserFormInput.apply)(UserFormInput.unapply)
    )
  }

  def userList: Action[AnyContent] = UserAction.async { implicit request =>
    logger.trace("user list: ")
    userResourceHandler.findAll.map { users =>
      Ok(Json.toJson(users))
    }
  }

  def create: Action[AnyContent] = UserAction.async { implicit request =>
    logger.trace("create: ")
    processJsonUser()
  }

  def show(id: Int): Action[AnyContent] = UserAction.async {
    implicit request =>
      logger.trace(s"show: id = $id")
      userResourceHandler.find(id).map { user =>
        Ok(Json.toJson(user))
      }
  }

  def update(id: Int): Action[AnyContent] = UserAction.async {
    implicit request =>
      logger.trace(s"update: id = $id")
      updateJsonUser(id)
  }

  def delete(id: Int): Action[AnyContent] = UserAction.async {
    implicit request =>
      logger.trace(s"show: id = $id")
      deleteUser(id)
  }

  private def processJsonUser[A]()(implicit request: UserRequest[A]): Future[Result] = {
    def failure(badForm: Form[UserFormInput]) = {
      Future.successful(BadRequest(badForm.errorsAsJson))
    }

    def success(input: UserFormInput) = {
      userResourceHandler.create(input).map { user_id =>
        Created(Json.toJson(user_id))
      }
    }
    form.bindFromRequest().fold(failure, success)
  }

  private def updateJsonUser[A](id: Int)(implicit request: UserRequest[A]): Future[Result] = {
    def failure(badForm: Form[UserFormInput]) = {
      Future.successful(BadRequest(badForm.errorsAsJson))
    }

    def success(input: UserFormInput) = {
      userResourceHandler.update(id, input).map { userExists: Boolean =>
        val result = if (userExists){
          NoContent
        }else {
          NotFound
        }
        result
      }
    }

    form.bindFromRequest().fold(failure, success)
  }

  private def deleteUser[A](id: Int)(implicit request: UserRequest[A]): Future[Result] = {
    userResourceHandler.delete(id).map { userExists: Boolean =>
      val result = if (userExists){
        NoContent
      }else {
        NotFound
      }
      result
    }
  }
}
