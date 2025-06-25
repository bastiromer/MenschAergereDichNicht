package Persistence.FileIO

import model.GameField
import play.api.libs.json.{JsValue, Json, Writes}
import util.json.JsonReaders.*
import util.json.JsonWriters.*

import java.io.{File, FileNotFoundException, IOException, PrintWriter}
import java.nio.file.{Files, Paths}
import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source

case class JsonFileIO() extends FileIO:
  private val path = "Persistence/saveGameJson"
  private val fileLock = new Object

  override def save(gameField: GameField, target: String): Unit =
    fileLock.synchronized {
      createFolderIfNotExists()
      val pw = new PrintWriter(new File(path + "/" + target + ".json"))
      val json = Json.toJson(gameField)
      pw.write(Json.stringify(json))
      pw.close()
    }

  def load(source: String): GameField =
    fileLock.synchronized {
      createFolderIfNotExists()
      val filePath: String = path + "/" + source + ".json"
      if (!Files.exists(Paths.get(filePath))) {
        throw new FileNotFoundException("File not found: " + filePath)
      }
      val file = Source.fromFile(filePath)
      try {
        Json.fromJson(Json.parse(file.mkString)).get
      } finally {
        file.close()
      }
    }

  override def getTargets: List[String] =
    createFolderIfNotExists()
    val files: List[File] = File(path).listFiles().toList
    files.map(file => file.toString.replaceAll(".json", "").replaceAll(path + "/", ""))

  private def createFolderIfNotExists(): Unit = {
    val folder = new File(path)
    if (!folder.exists()) {
      Files.createDirectories(Paths.get(path))
    }
  }
