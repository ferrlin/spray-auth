package scalapenos.spray.auth

case class Id(value: String) {
  override def toString = value
}

object Id {
  import akka.http.scaladsl.unmarshalling._
  import akka.http.scaladsl._
  import akka.http.scaladsl.server.PathMatchers._
  import akka.http.scaladsl.server._
  import spray.json._
  import scala.concurrent.Future
  import scala.concurrent.ExecutionContext.Implicits.global

  def random: Id = Id(java.util.UUID.randomUUID().toString)

  // implicit object IdDeserializer extends FromStringDeserializer[Id] {
  // def apply(value: String) = Right(Id(value))
  // []}

  /* Still figuring out how to port this to akka-http dsl */
  // implicit object IdDeserializer extends FromStringUnmarshaller[Id] {
  // def apply(value: String) = Future(Id(value))
  // }

  implicit object IdJsonFormat extends JsonFormat[Id] {
    def write(id: Id) = JsString(id.value)
    def read(json: JsValue) = json match {
      case JsString(value) => Id(value)
      case other => deserializationError(s"Expected Id as JsString, but got ${other}")
    }
  }

  /* A Segment PathMatcher converted to an Id.  */
  val IdSegment: PathMatcher1[Id] = Segment.flatMap(segment => Some(Id(segment)))
}

trait ExecutionContextProvider {
  import scala.concurrent.ExecutionContext

  implicit def executionContext: ExecutionContext
}

trait ActorExecutionContextProvider extends ExecutionContextProvider { this: akka.actor.Actor =>
  implicit def executionContext = context.dispatcher
}
