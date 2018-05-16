package api.users

import javax.inject.Inject

import play.api.Logger
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

case class UserFormInput(name:String, login: String, password: String)

class UsersController @Inject()(cc: UserControllerComponents)(implicit ec: ExecutionContext)
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

  def index: Action[AnyContent] = UserAction.async { implicit request =>
    logger.trace("index: ")
    postResourceHandler.find.map { posts =>
      Ok(Json.toJson(posts))
    }
  }

  def process: Action[AnyContent] = UserAction.async { implicit request =>
    logger.trace("process: ")
    processJsonUser()
  }

  def show(id: String): Action[AnyContent] = UserAction.async { implicit request =>
    logger.trace(s"show: id = $id")
    postResourceHandler.lookup(id).map { post =>
      Ok(Json.toJson(post))
    }
  }

  private def processJsonUser[A]()(implicit request: UserRequest[A]): Future[Result] = {
    def failure(badForm: Form[UserFormInput]) = {
      Future.successful(BadRequest(badForm.errorsAsJson))
    }

    def success(input: UserFormInput) = {
      postResourceHandler.create(input).map { post =>
        Created(Json.toJson(post))
      }
    }

    form.bindFromRequest().fold(failure, success)
  }
}
