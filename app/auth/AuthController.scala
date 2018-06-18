package auth

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import auth.AuthHelpers._
import javax.inject._
import play.api.data.Form
import play.api.mvc._
import auth.session.SessionService


/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class AuthController @Inject()(
    userAction: UserInfoAction,
    sessionGenerator: SessionGenerator,
    sessionService: SessionService,
    cc: ControllerComponents) extends AbstractController(cc) {

  def login(): Action[AnyContent] = userAction.async { implicit request: UserRequest[AnyContent] =>
    val successFunc = { userInfo: UserInfo =>
      sessionGenerator.createSession(userInfo).map {
        case (sessionId, encryptedCookie) =>
          val session = request.session + (SESSION_ID -> sessionId)
          Ok("Login Successfull")
            .withSession(session)
            .withCookies(encryptedCookie)
      }
    }

    val errorFunc = { badForm: Form[UserInfo] =>
      Future.successful {
        BadRequest("").flashing(FLASH_ERROR -> "Could not login!")
      }
    }

    form.bindFromRequest().fold(errorFunc, successFunc)
  }

  def logout: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    // When we delete the session id, removing the secret key is enough to render the
    // user info cookie unusable.
    request.session.get(SESSION_ID).foreach { sessionId =>
      sessionService.delete(sessionId)
    }

    discardingSession {
      Ok("Logout successful")
    }
  }
}

