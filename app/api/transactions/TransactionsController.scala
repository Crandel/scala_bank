package api.transactions

import scala.concurrent.{ExecutionContext, Future}

import javax.inject.Inject
import play.api.Logger
import play.api.data.Form
import play.api.data.validation.Constraints._
import play.api.libs.json.Json
import play.api.mvc._


case class TransactionFormInput(source_id: Int, destination_id: Int, amount: Double)

class TransactionsController @Inject()(cc: TransactionControllerComponents)(implicit ec: ExecutionContext)
    extends TransactionBaseController(cc) {

  private val logger = Logger(getClass)

  private val form: Form[TransactionFormInput] = {
    import play.api.data.Forms._
    import play.api.data.format.Formats._

    Form(
      mapping(
        "source_id" -> number(min = 0),
        "destination_id" -> number(min = 0),
        "amount" -> of[Double].verifying(min(0.0))
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
    currencyResourceHandler.findAll.map { currency =>
      Ok(Json.toJson(currency))
    }
  }

  def process: Action[AnyContent] = TransactionAction.async { implicit request =>
    logger.trace("process: ")
    processJsonTransaction()
  }

  def show(id: Int): Action[AnyContent] = TransactionAction.async {
    implicit request =>
      logger.trace(s"show: id = $id")
      transactionResourceHandler.find(id).map { transaction =>
        Ok(Json.toJson(transaction))
      }
  }

  def showCurrency(id: Int): Action[AnyContent] = TransactionAction.async {
    implicit request =>
      logger.trace(s"show currency: id = $id")
      currencyResourceHandler.get(id).map { post =>
        Ok(Json.toJson(post))
      }
  }

  private def processJsonTransaction[A]()(implicit request: TransactionRequest[A]): Future[Result] = {
    def failure(badForm: Form[TransactionFormInput]) = {
      Future.successful(BadRequest(badForm.errorsAsJson))
    }

    def success(input: TransactionFormInput) = {
      transactionResourceHandler.create(input).map {
        case Some(id) => Created(Json.toJson(id))
        case _ => BadRequest("Not enough money in source account")
      }
    }
    form.bindFromRequest().fold(failure, success)
  }
}
