package core.controllerComponents.util

trait Observer:
  def id: String
  def update(): Unit

class ObserverHttp(observerUrl: String) extends Observer:

  import scala.io.Source
  import scala.util.Using

  override def id: String = observerUrl
  override def update(): Unit =
    val requestUrl = observerUrl
    Using.resource(Source.fromURL(requestUrl)) { source =>
      val response = source.getLines().mkString
      println(s"Observer [$observerUrl] responded: $response")
    }

trait Observable:
  private var subscribers: Map[String, Observer] = Map()
  def add(s: Observer) = subscribers += (s.id -> s)
  def remove(id: String) = subscribers -= id
  def notifyObservers() = subscribers.values.foreach(_.update())
