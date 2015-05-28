package scalapenos.spray

package object auth {

  import akka.http.scaladsl.server.RequestContext
  import akka.http.scaladsl.server.Rejection
  import scala.concurrent.Future

  type Authentication[T] = Either[Rejection, T]
  type ContextAuthenticator[T] = RequestContext => Future[Authentication[T]]
}