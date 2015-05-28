package scalapenos.spray
package auth

import concurrent._
import concurrent.Future._

import akka.http.scaladsl.server.RequestContext
import akka.http.scaladsl.server.Rejection

/**
 * Simple wrapper around the Spray routing ContextAuthenticator type to
 * make them composable.
 *
 * Spray types used by Authenticators (defined in spray.routing.authentication):
 *
 *   type Authentication[T] = Either[Rejection, T]
 *   type ContextAuthenticator[T] = RequestContext => Future[Authentication[T]]
 *
 */
abstract class Authenticator[T] extends ContextAuthenticator[T] {
  /**
   * Function to make Authenticators composable, i.e. to create a new Authenticator
   * that wraps two others and that will try the second one if the first one fails
   * to authenticate the request.
   */
  def orElse(other: Authenticator[T])(implicit ec: ExecutionContext): Authenticator[T] = {
    new Authenticator[T] {
      def apply(requestContext: RequestContext): Future[Authentication[T]] = {
        // We need to explicitly specify the 'super' apply method from the surrounding
        // class so we can call it without calling ourselves recursively by accident
        Authenticator.this.apply(requestContext).flatMap {
          case Left(rejection) => other.apply(requestContext)
          case success @ Right(_) => successful(success)
        }
      }
    }
  }
}

/** If anything goes wrong during authentication, this is the rejection to use. */
case object AuthenticatorRejection extends Rejection

/** Custom RejectionHandler for dealing with AuthenticatorRejections. */
// object AuthenticatorRejectionHandler {
//   import spray.http.StatusCodes._
//   import AuthDirectives._

//   // TODO: make this return JSON content
//   def apply(settings: Settings): RejectionHandler = RejectionHandler {
//     case AuthenticatorRejection :: _ => {
//       // completeWithoutSessionCookies(settings.Auth.CookieDomain, settings.Auth.EnforceTLS)(Unauthorized, "Missing or invalid authentication")
//     }
//   }
// }
