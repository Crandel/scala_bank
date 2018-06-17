package api.transactions

import javax.inject.Inject
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

class TransactionsRouter @Inject()(controller: TransactionsController) extends SimpleRouter {
  val prefix = "/api/transactions"

  override def routes: Routes = {
    case GET(p"/currencies/") =>
      controller.currencies

    case GET(p"/currencies/${int(id)}") =>
      controller.showCurrency(id)

    case GET(p"/") =>
      controller.index

    case POST(p"/") =>
      controller.process

    case GET(p"/${int(id)}") =>
      controller.show(id)
  }
}
