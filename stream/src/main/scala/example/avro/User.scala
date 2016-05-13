/** MACHINE-GENERATED FROM AVRO SCHEMA. DO NOT EDIT DIRECTLY */
package example.avro

case class User(var name: String, var favorite_number: Option[Int], var favorite_color: String) extends org.apache.avro.specific.SpecificRecordBase {
  def this() = this("", None, "")
  def get(field$: Int): AnyRef = {
    field$ match {
      case pos if pos == 0 => {
        name
      }.asInstanceOf[AnyRef]
      case pos if pos == 1 => {
        favorite_number match {
          case Some(x) => x
          case None => null
        }
      }.asInstanceOf[AnyRef]
      case pos if pos == 2 => {
        favorite_color
      }.asInstanceOf[AnyRef]
      case _ => new org.apache.avro.AvroRuntimeException("Bad index")
    }
  }
  def put(field$: Int, value: Any): Unit = {
    field$ match {
      case pos if pos == 0 => this.name = {
        value.toString
      }.asInstanceOf[String]
      case pos if pos == 1 => this.favorite_number = {
        value match {
          case null => None
          case _ => Some(value)
        }
      }.asInstanceOf[Option[Int]]
      case pos if pos == 2 => this.favorite_color = {
        value.toString
      }.asInstanceOf[String]
      case _ => new org.apache.avro.AvroRuntimeException("Bad index")
    }
    ()
  }
  def getSchema: org.apache.avro.Schema = User.SCHEMA$
}

object User {
  val SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"User\",\"namespace\":\"example.avro\",\"fields\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"favorite_number\",\"type\":[\"int\",\"null\"]},{\"name\":\"favorite_color\",\"type\":\"string\"}]}")
}