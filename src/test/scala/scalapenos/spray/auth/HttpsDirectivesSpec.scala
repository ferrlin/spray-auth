package scalapenos.spray.auth
package web

// import org.specs2.mutable.Specification
import org.scalatest._
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
// import akka.http.scaladsl.testkit.ScalatestRouteTest
// import akka.http.scaladsl.testkit.RouteSpec
import akka.http.scaladsl.testkit.ScalatestRouteTest
// import spray.testkit.Specs2RouteTest
// import akka.http.scaladsl.testkit.ScalatestRouteTest

import HttpsDirectives._

class HttpsDirectivesSpec extends FlatSpec
  with Matchers
  with ScalatestRouteTest
  with HttpsDirectives {

  val httpUri = Uri("http://example.com/api/awesome")
  val httpsUri = Uri("https://example.com/api/awesome")

  val route = enforceHttps {
    complete(OK)
  }

  /* Scalatest version*/
  "The enforceHttps Directive" should "allow https requests and respond with the HSTS header" in {
    Get(httpsUri) ~> route ~> check {
      status === OK
      header(StrictTransportSecurity.name) shouldBe Some(StrictTransportSecurity)
    }
  }

  it should "allow terminated https requests containing a 'X-Forwarded-Proto' header and respond with the HSTS header" in {
    Get(httpUri) ~> addHeader(RawHeader("X-Forwarded-Proto", "https")) ~> route ~> check {
      status === OK
      header(StrictTransportSecurity.name) shouldBe Some(StrictTransportSecurity)
    }
  }

  it should "redirect plain http request to the matching https URI" in {
    Get(httpUri) ~> route ~> check {
      status === MovedPermanently
      header[Location].map(l => Uri(l.value)) shouldBe Some(httpsUri)
      header(StrictTransportSecurity.name) shouldBe Some(StrictTransportSecurity)
    }
  }

  it should "also redirect terminated http request to the matching https URI" in {
    Get(httpUri) ~> addHeader(RawHeader("X-Forwarded-Proto", "http")) ~> route ~> check {
      status === MovedPermanently
      header[Location].map(l => Uri(l.value)) shouldBe Some(httpsUri)
      header(StrictTransportSecurity.name) shouldBe Some(StrictTransportSecurity)
    }
  }

  val route2 = enforceHttpsIf(true) {
    complete(OK)
  }

  "The enforceHttpsIf directive" should "enforce https when the argument resolves to true" in {
    Get(httpsUri) ~> route2 ~> check {
      status === OK
      header(StrictTransportSecurity.name) shouldBe Some(StrictTransportSecurity)
    }

    Get(httpUri) ~> route2 ~> check {
      status === MovedPermanently
      header[Location].map(l => Uri(l.value)) shouldBe Some(httpsUri)
      header(StrictTransportSecurity.name) shouldBe Some(StrictTransportSecurity)
    }
  }

  val route3 = enforceHttpsIf(false) {
    complete(OK)
  }

  it should "not enforce https when the argument resolves to false" in {
    Get(httpsUri) ~> route3 ~> check {
      status === OK
      header(StrictTransportSecurity.name) shouldBe None
    }
    Get(httpUri) ~> route3 ~> check {
      status === OK
      header(StrictTransportSecurity.name) shouldBe None
    }
  }
}
