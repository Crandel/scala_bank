package api.transactions

import scala.concurrent.{ExecutionContext, Future}

import javax.inject.Inject
import play.api.Logger
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._


case class TransactionFormInput(source_id: String, destination_id: String, amount: Double)

class TransactionsController @Inject()(cc: TransactionControllerComponents)(implicit ec: ExecutionContext)
    extends TransactionBaseController(cc) {

  private val logger = Logger(getClass)

  private val form: Form[TransactionFormInput] = {
    import play.api.data.Forms._
    import play.api.data.format.Formats._

    Form(
      mapping(
        "source_id" -> nonEmptyText,
        "destination_id" -> nonEmptyText,
        "amount" -> of[Double]
      )(TransactionFormInput.apply)(TransactionFormInput.unapply)
    )
  }

  def index: Action[AnyContent] = TransactionAction.async { implicit request =>
    logger.trace("index: ")
    transactionResourceHandler.takeList.map { transactions =>
      Ok(Json.toJson(transactions))
    }
  }

  def currencies: Action[AnyContent] = TransactionAction.async {implicit request =>
    logger.trace("currencies: ")
    currencyResourceHandler.find.map { currency =>
      Ok(Json.toJson(currency))
    }
  }

  def process: Action[AnyContent] = TransactionAction.async { implicit request =>
    logger.trace("process: ")
    processJsonTransaction()
  }

  def show(id: String): Action[AnyContent] = TransactionAction.async {
    implicit request =>
      logger.trace(s"show: id = $id")
      transactionResourceHandler.find(id).map { transaction =>
        Ok(Json.toJson(transaction))
      }
  }

  def showCurrency(id: String): Action[AnyContent] = TransactionAction.async {
    implicit request =>
      logger.trace(s"show currency: id = $id")
      currencyResourceHandler.lookup(id).map { post =>
        Ok(Json.toJson(post))
      }
  }

  def update(id: String): Action[AnyContent] = TransactionAction.async {
    implicit request =>
      logger.trace(s"update: id = $id")
      updateJsonTransaction(id)
  }

  def delete(id: String): Action[AnyContent] = TransactionAction.async {
    implicit request =>
      logger.trace(s"show: id = $id")
      deleteTransaction(id)
  }

  private def processJsonTransaction[A]()(implicit request: TransactionRequest[A]): Future[Result] = {
    def failure(badForm: Form[TransactionFormInput]) = {
      Future.successful(BadRequest(badForm.errorsAsJson))
    }

    def success(input: TransactionFormInput) = {
      transactionResourceHandler.create(input).map { transaction_id =>
        Created(Json.toJson(transaction_id))
      }
    }
    form.bindFromRequest().fold(failure, success)
  }

  private def updateJsonTransaction[A](id: String)(implicit request: TransactionRequest[A]): Future[Result] = {
    def failure(badForm: Form[TransactionFormInput]) = {
      Future.successful(BadRequest(badForm.errorsAsJson))
    }

    def success(input: TransactionFormInput) = {
      transactionResourceHandler.update(id, input).map { transactionExists: Boolean =>
        val result = if (transactionExists){
          NoContent
        }else {
          NotFound
        }
        result
      }
    }

    form.bindFromRequest().fold(failure, success)
  }

  private def deleteTransaction[A](id: String)(implicit request: TransactionRequest[A]): Future[Result] = {
    transactionResourceHandler.delete(id).map { transactionExists: Boolean =>
        val result = if (transactionExists){
          NoContent
        }else {
          NotFound
        }
        result
    }
  }
}
