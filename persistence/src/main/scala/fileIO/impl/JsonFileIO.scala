package fileIO.impl

import fileIO.FileIO
import model.GameField
import util.json.JsonWriters.gameFieldWrites
import util.json.JsonReaders.gameFieldReads

import play.api.libs.json.Json
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source

import java.io.{File, FileNotFoundException, PrintWriter}
import java.nio.file.{Files, Paths}


case class JsonFileIO() extends FileIO:
  private val path = "saveGameJson"
  override def save(gameField: GameField, target: String): Unit =
    createFolderIfNotExists()
    val pw = new PrintWriter(new File(path + "/" + target + ".json"))
    val json = Json.toJson(gameField)
    pw.write(Json.stringify(json))
    pw.close()

  def load(source: String): Future[GameField] = Future {
    createFolderIfNotExists()
    val filePath: String = path + "/" + source + ".json"
    if (!Files.exists(Paths.get(filePath))) {
      throw new FileNotFoundException("File not found: " + filePath)
    }
    val file = Source.fromFile(filePath)
    Json.fromJson(Json.parse(file.mkString)).get
  }
  

  override def getTargets: List[String] =
    createFolderIfNotExists()
    val files: List[File] = File(path).listFiles().toList
    files.map(file => file.toString.replaceAll(".json", "").replaceAll(path + "/", ""))

  private def createFolderIfNotExists(): Unit =
    val folder = new File(path)
    if (!folder.exists) {
      Files.createDirectory(Paths.get(path))
    }

