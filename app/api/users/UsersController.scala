package api.users

import javax.inject.Inject

import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

case class UserFormInput(title: String, body: String)

/**
  * Takes HTTP requests and produces JSON.
  */
class UserController @Inject()(
    action: UserAction,
    handler: UsersResourceHandler)(implicit ec: ExecutionContext)
    extends Controller {

  private val form: Form[UserFormInput] = {
    import play.api.data.Forms._

    Form(
      mapping(
        "title" -> nonEmptyText,
        "body" -> text
      )(UserFormInput.apply)(UserFormInput.unapply)
    )
  }

  def index: Action[AnyContent] = {
    action.async { implicit request =>
      handler.find.map { posts =>
        Ok(Json.toJson(posts))
      }
    }
  }

  def process: Action[AnyContent] = {
    action.async { implicit request =>
      processJsonPost()
    }
  }

  def show(id: String): Action[AnyContent] = {
    action.async { implicit request =>
      handler.lookup(id).map { post =>
        Ok(Json.toJson(post))
      }
    }
  }

  private def processJsonPost[A]()(
      implicit request: UsersRequest[A]): Future[Result] = {
    def failure(badForm: Form[UserFormInput]) = {
      Future.successful(BadRequest(badForm.errorsAsJson))
    }

    def success(input: UserFormInput) = {
      handler.create(input).map { post =>
        Created(Json.toJson(post)).withHeaders(LOCATION -> post.link)
      }
    }

    form.bindFromRequest().fold(failure, success)
  }
}
