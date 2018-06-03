package api.transactions

import db.TransactionId
import javax.inject.Inject
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

class TransactionsRouter @Inject()(controller: TransactionsController) extends SimpleRouter {
  val prefix = "/api/transactions"

  def link(id: TransactionId): String = {
    import io.lemonlabs.uri.dsl._
    val url = prefix / id.toString
    url.toString()
  }

  override def routes: Routes = {
    case GET(p"/currencies/") =>
      controller.currencies

    case GET(p"/currencies/$id") =>
      controller.showCurrency(id)

    case GET(p"/") =>
      controller.index

    case POST(p"/") =>
      controller.process

    case GET(p"/$id") =>
      controller.show(id)

    case PATCH(p"/$id") =>
      controller.update(id)

    case DELETE(p"/$id") =>
      controller.delete(id)
  }
}
