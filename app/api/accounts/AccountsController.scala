package api.accounts

import javax.inject.Inject

import play.api.Logger
import play.api.data.Form
import play.api.data.validation.Constraints._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

case class AccountFormInput(user_id: Int, currency_id: Int, balance: Double)

class AccountsController @Inject()(cc: AccountControllerComponents)(implicit ec: ExecutionContext)
    extends AccountBaseController(cc) {

  private val logger = Logger(getClass)

  private val form: Form[AccountFormInput] = {
    import play.api.data.Forms._
    import play.api.data.format.Formats._

    Form(
      mapping(
        "user_id" -> number(min = 0),
        "currency_id" -> number(min = 0),
        "balance" -> of[Double].verifying(min(0.0))
      )(AccountFormInput.apply)(AccountFormInput.unapply)
    )
  }

  def list: Action[AnyContent] = AccountAction.async { implicit request =>
    logger.trace("index: ")
    accountResourceHandler.findAll.map { accounts =>
      Ok(Json.toJson(accounts))
    }
  }

  def create: Action[AnyContent] = AccountAction.async { implicit request =>
    logger.trace("create: ")
    processJsonAccount()
  }

  def get(id: Int): Action[AnyContent] = AccountAction.async {
    implicit request =>
      logger.trace(s"show: id = $id")
      accountResourceHandler.find(id).map { account =>
        Ok(Json.toJson(account))
      }
  }

  def update(id: Int): Action[AnyContent] = AccountAction.async {
    implicit request =>
      logger.trace(s"update: id = $id")
      updateJsonAccount(id)
  }

  def delete(id: Int): Action[AnyContent] = AccountAction.async {
    implicit request =>
      logger.trace(s"delete: id = $id")
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

  private def updateJsonAccount[A](id: Int)(implicit request: AccountRequest[A]): Future[Result] = {
    def failure(badForm: Form[AccountFormInput]) = {
      Future.successful(BadRequest(badForm.errorsAsJson))
    }

    def success(input: AccountFormInput) = {
      accountResourceHandler.update(id, input).map { accountExists: Boolean =>
        if (accountExists){
          NoContent
        }else {
          NotFound
        }
      }
    }

    form.bindFromRequest().fold(failure, success)
  }

  private def deleteAccount[A](id: Int)(implicit request: AccountRequest[A]): Future[Result] = {
    accountResourceHandler.delete(id).map { accountExists: Boolean =>
        if (accountExists){
          NoContent
        }else {
          NotFound
        }
    }
  }
}
