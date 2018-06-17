package api.users

import javax.inject.Inject
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

class UsersRouter @Inject()(controller: UsersController) extends SimpleRouter {
  val prefix = "/api/users"

  override def routes: Routes = {
    case GET(p"/") =>
      controller.userList

    case POST(p"/") =>
      controller.create

    case GET(p"/${int(id)}") =>
      controller.show(id)

    case PATCH(p"/${int(id)}") =>
      controller.update(id)

    case DELETE(p"/${int(id)}") =>
      controller.delete(id)
  }
}
