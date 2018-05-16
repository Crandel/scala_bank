package api.auth

import javax.inject._
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class AuthController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def signing() = Action { implicit request: Request[AnyContent] =>
    Ok("signing")
  }

  def login() = Action { implicit request: Request[AnyContent] =>
    Ok("Login")
  }

  def logout() = Action { implicit request: Request[AnyContent] =>
    Ok("Logout")
  }

}

