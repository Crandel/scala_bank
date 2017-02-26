package api

import play.api.i18n.Messages

/**
  * Package object for post.  This is a good place to put implicit conversions.
  */
package object post {

  /**
    * Converts between UsersRequest and Messages automatically.
    */
  implicit def requestToMessages[A](implicit r: UsersRequest[A]): Messages = {
    r.messages
  }
}
