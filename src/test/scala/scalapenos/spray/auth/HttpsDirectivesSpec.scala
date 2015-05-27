package scalapenos.spray.auth
package web

import org.specs2.mutable.Specification

// import spray.http._
// import spray.http.HttpHeaders._
import akka.http.scaladsl.model.headers._
// import spray.http.StatusCodes._
import akka.http.scaladsl.model.StatusCodes._
// import spray.routing._
import akka.http.scaladsl.server._
import akka.http.scaladsl.model._
// import spray.routing.Directives._
import akka.http.scaladsl.server.Directives._
// import spray.testkit.Specs2RouteTest
// import akka.http.scaladsl.testkit.RouteTest
// import akka.http.scaladsl.testkit.ScalatestRouteTest

import HttpsDirectives._

class HttpsDirectivesSpec extends Specification
  with RouteTest
  with HttpsDirectives {

  val httpUri = Uri("http://example.com/api/awesome")
  val httpsUri = Uri("https://example.com/api/awesome")

  /*"The enforceHttps directive" should {
    val route = enforceHttps {
      complete(OK)
    }

    "allow https requests and respond with the HSTS header" in {
      Get(httpsUri) ~> route ~> check {
        status === OK
        header(StrictTransportSecurity.name) must beSome(StrictTransportSecurity)
      }
    }

    "allow terminated https requests containing a 'X-Forwarded-Proto' header and respond with the HSTS header" in {
      Get(httpUri) ~> addHeader(RawHeader("X-Forwarded-Proto", "https")) ~> route ~> check {
        status === OK
        header(StrictTransportSecurity.name) must beSome(StrictTransportSecurity)
      }
    }

    "redirect plain http requests to the matching https URI" in {
      Get(httpUri) ~> route ~> check {
        status === MovedPermanently
        header[Location].map(l => Uri(l.value)) must beSome(httpsUri)
        header(StrictTransportSecurity.name) must beSome(StrictTransportSecurity)
      }
    }

    "redirect terminated http requests to the matching https URI" in {
      Get(httpUri) ~> addHeader(RawHeader("X-Forwarded-Proto", "http")) ~> route ~> check {
        status === MovedPermanently
        header[Location].map(l => Uri(l.value)) must beSome(httpsUri)
        header(StrictTransportSecurity.name) must beSome(StrictTransportSecurity)
      }
    }
  }

  "The enforceHttpsIf directive" should {
    "enforce https when the argument resolves to true" in {
      val route = enforceHttpsIf(true) {
        complete(OK)
      }

      Get(httpsUri) ~> route ~> check {
        status === OK
        header(StrictTransportSecurity.name) must beSome(StrictTransportSecurity)
      }

      Get(httpUri) ~> route ~> check {
        status === MovedPermanently
        header[Location].map(l => Uri(l.value)) must beSome(httpsUri)
        header(StrictTransportSecurity.name) must beSome(StrictTransportSecurity)
      }
    }

    "not enforce https when the argument resolves to false" in {
      val route = enforceHttpsIf(false) {
        complete(OK)
      }

      Get(httpsUri) ~> route ~> check {
        status === OK
        header(StrictTransportSecurity.name) must beNone
      }

      Get(httpUri) ~> route ~> check {
        status === OK
        header(StrictTransportSecurity.name) must beNone
      }
    }
  }*/

}
