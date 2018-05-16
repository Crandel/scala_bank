package api.users

import javax.inject.Inject
import play.api.Logger

import scala.concurrent.ExecutionContext

class UsersController  @Inject()(implicit ec: ExecutionContext){

  private val logger = Logger(getClass)
}
