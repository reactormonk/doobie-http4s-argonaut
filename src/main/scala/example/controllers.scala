package controllers

import argonaut._, Argonaut._, Shapeless._

import org.http4s._
import org.http4s.dsl._
import org.http4s.server.syntax._
import org.http4s.argonaut._
import org.http4s.server.blaze._
import org.http4s.server.{Server, ServerApp}

import org.joda.time._
import doobie.imports._
import scalaz.concurrent.Task

case class User(id: Long, name: String, email: String, createdAt: DateTime)

object User {
  // A DateTime is just a String that's a bit more complicated on the scala end, so we reuse the String codec.
  implicit val dateTimeCodec: CodecJson[DateTime] = CodecJson.derived[String].xmap(string => DateTime.parse(string))(dateTime => dateTime.toString)

  // Trivial dependency injection - pass it in.
  def userService(transactor: Transactor[Task]) = HttpService {
    case GET -> Root / "users" =>
      Ok().withBody(
        // Calling process/transact as described in http://tpolecat.github.io/doobie-0.3.0/04-Selecting.html
        transactor.transact(models.User.findAll.process)
          // Converting the results to json - streaming and line by line.
          // Can't stream json yet... https://github.com/http4s/http4s/blob/master/argonaut/src/main/scala/org/http4s/argonaut/ArgonautInstances.scala
          .map(_.convert.asJson)
      )
    case GET -> Root / "users" / LongVar(id) =>
      // We're getting a Task[Option[User]] back, so map and pattern match.
      transactor.transact(models.User.find(id)).flatMap({
        case Some(user) => Ok().withBody(user.convert.asJson)
        case None => NotFound()
      })
  }
}

object Hello {
  val hello = HttpService {
    case GET -> Root / "hello" => Ok().withBody("""{"Hello": "World"}""")
  }
}

// http://http4s.org/docs/0.15/service.html#running-your-service
object Application extends ServerApp {
  // http://tpolecat.github.io/doobie-0.3.0/03-Connecting.html
  val xa = DriverManagerTransactor[Task]("org.postgresql.Driver", "jdbc:postgresql:world", "postgres", "")

  override def server(args: List[String]): Task[Server] = {
    BlazeBuilder
      .bindHttp(8080, "localhost")
      .mountService(User.userService(xa) orElse Hello.hello, "/")
      .start
  }
}
