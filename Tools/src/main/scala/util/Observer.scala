package util

trait Observer:
  def update(): Unit

trait Observable:
  private var subscribers: Vector[Observer] = Vector()
  def add(s: Observer): Unit = subscribers = subscribers :+ s
  def notifyObservers(): Unit = subscribers.foreach(o => o.update())