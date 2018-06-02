package api.accounts

import javax.inject.Inject

import play.api.Logger
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

case class AccountFormInput(user_id: String, currency_id: String, balance: Double)

class AccountsController @Inject()(cc: AccountControllerComponents)(implicit ec: ExecutionContext)
    extends AccountBaseController(cc) {

  private val logger = Logger(getClass)

  private val form: Form[AccountFormInput] = {
    import play.api.data.Forms._
    import play.api.data.format.Formats._

    Form(
      mapping(
        "account_id" -> nonEmptyText,
        "currency_id" -> nonEmptyText,
        "balance" -> of[Double]
      )(AccountFormInput.apply)(AccountFormInput.unapply)
    )
  }

  def index: Action[AnyContent] = AccountAction.async { implicit request =>
    logger.trace("index: ")
    accountResourceHandler.takeList.map { accounts =>
      Ok(Json.toJson(accounts))
    }
  }

  def process: Action[AnyContent] = AccountAction.async { implicit request =>
    logger.trace("process: ")
    processJsonAccount()
  }

  def show(id: String): Action[AnyContent] = AccountAction.async {
    implicit request =>
      logger.trace(s"show: id = $id")
      accountResourceHandler.find(id).map { account =>
        Ok(Json.toJson(account))
      }
  }

  def update(id: String): Action[AnyContent] = AccountAction.async {
    implicit request =>
      logger.trace(s"update: id = $id")
      updateJsonAccount(id)
  }

  def delete(id: String): Action[AnyContent] = AccountAction.async {
    implicit request =>
      logger.trace(s"show: id = $id")
      deleteAccount(id)
  }

  private def processJsonAccount[A]()(implicit request: AccountRequest[A]): Future[Result] = {
    def failure(badForm: Form[AccountFormInput]) = {
      Future.successful(BadRequest(badForm.errorsAsJson))
    }

    def success(input: AccountFormInput) = {
      accountResourceHandler.create(input).map { account_id =>
        Created(Json.toJson(account_id))
      }
    }
    form.bindFromRequest().fold(failure, success)
  }

  private def updateJsonAccount[A](id: String)(implicit request: AccountRequest[A]): Future[Result] = {
    def failure(badForm: Form[AccountFormInput]) = {
      Future.successful(BadRequest(badForm.errorsAsJson))
    }

    def success(input: AccountFormInput) = {
      accountResourceHandler.update(id, input).map { accountExists: Boolean =>
        val result = if (accountExists){
          NoContent
        }else {
          NotFound
        }
        result
      }
    }

    form.bindFromRequest().fold(failure, success)
  }

  private def deleteAccount[A](id: String)(implicit request: AccountRequest[A]): Future[Result] = {
    accountResourceHandler.delete(id).map { accountExists: Boolean =>
        val result = if (accountExists){
          NoContent
        }else {
          NotFound
        }
        result
    }
  }
}
