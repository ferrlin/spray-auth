package scalapenos.spray.auth

// import spray.routing._
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._
// import spray.routing.Directives._
import akka.http.scaladsl.model.headers.RawHeader
// import spray.http.HttpHeaders.RawHeader
import akka.http.scaladsl.model.StatusCode
// import spray.http.StatusCode
import akka.http.scaladsl.model.StatusCodes._
// import spray.http.StatusCodes._

trait HttpsDirectives {
  import HttpsDirectives._

  def enforceHttpsIf(yes: => Boolean): Directive0 = {
    if (yes) enforceHttps
    else pass
  }

  def enforceHttps: Directive0 = {
    respondWithHeader(StrictTransportSecurity) &
      extract(isHttpsRequest).flatMap(
        if (_) pass
        else redirectToHttps)
  }

  def redirectToHttps: Directive0 = {
    // requestUri.flatMap { uri =>
    // redirect(uri.copy(scheme = "https"), MovedPermanently)
    // }
    extractUri.flatMap { uri =>
      redirect(uri.copy(scheme = "https"), MovedPermanently)
    }
  }

}

object HttpsDirectives {
  /** Hardcoded max-age of one year (31536000 seconds) for now. */
  private[auth] val StrictTransportSecurity = RawHeader("Strict-Transport-Security", "max-age=31536000")

  private[auth] val isHttpsRequest: RequestContext => Boolean = { ctx =>
    ctx.request.uri.scheme == "https" || ctx.request.headers.exists(h => h.is("x-forwarded-proto") && h.value == "https")
  }
}
