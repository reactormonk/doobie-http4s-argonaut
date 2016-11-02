package models
import org.joda.time._
import doobie.imports._
import scalaz._, Scalaz._
import scalaz.concurrent.Task

import Codecs._

trait Convertable[T] {
  def convert: T
}

case class User(id: Long, name: String, email: String, createdAt: LocalDate) extends Convertable[controllers.User] {
  def convert: controllers.User = controllers.User(id,
    name,
    email,
    createdAt.toDateTimeAtStartOfDay(DateTimeZone.getDefault)
  )
}

object User {
  val findAll = sql"select (id, name, email, createdAt) from users".query[User]
  def findQuery(id: Long) = sql"select (id, name, email, createdAt) from users where id = $id".query[User]
  def find(id: Long) = findQuery(id).option
}

object Codecs {
  // Takes a java.sql.Date and converts it into a joda.time.LocalDate - and back.
  implicit val localDateMeta: Meta[LocalDate] = Meta[java.sql.Date].nxmap(
    sql => LocalDate.fromDateFields(sql),
    joda => new java.sql.Date(joda.toDate.getTime)
  )
}
