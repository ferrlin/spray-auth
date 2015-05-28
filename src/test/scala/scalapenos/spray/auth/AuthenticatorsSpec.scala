package scalapenos.spray.auth

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future._

import org.specs2.mutable.Specification

// import spray.http._
import akka.http.scaladsl._
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.RequestContext
import akka.http.scaladsl.model.HttpRequest
// import spray.routing._
// import spray.util._
// 
class AuthenticatorsSpec extends Specification {
  // private val requestContext = RequestContext(HttpRequest(), null, null)
  /*
  "Authenticators" should {
    "be composable using orElse" in {
      val badAuth = new Authenticator[String] {
        def apply(requestContext: RequestContext) = successful(Left(AuthorizationFailedRejection))
      }

      val goodAuth = new Authenticator[String] {
        def apply(requestContext: RequestContext) = successful(Right("a user"))
      }

      val composed1: Authenticator[String] = badAuth orElse goodAuth
      val result1 = composed1(requestContext).await

      result1 === Right("a user")

      val composed2: Authenticator[String] = goodAuth orElse badAuth
      val result2 = composed2(requestContext).await

      result2 === Right("a user")
    }
  }
  */
}
