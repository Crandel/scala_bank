package api.accounts

import javax.inject.Inject
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

class AccountsRouter @Inject()(controller: AccountsController) extends SimpleRouter {
  val prefix = "/api/accounts"

  override def routes: Routes = {

    case POST(p"/") =>
      controller.create

    case GET(p"/list") =>
      controller.list

    case GET(p"/${int(id)}") =>
      controller.get(id)

    case PATCH(p"/${int(id)}") =>
      controller.update(id)

    case DELETE(p"/${int(id)}") =>
      controller.delete(id)
  }
}
