package FileIO

import model.GameField
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.io.{File, FileNotFoundException}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class JsonFileIOSpec extends AnyWordSpec with Matchers with BeforeAndAfterEach {
  val sut: JsonFileIO = JsonFileIO()

  override def beforeEach(): Unit = {
    val folder = new File("Persistence/saveGameJson")
    if (folder.exists()) {
      folder.listFiles().foreach(_.delete())
      folder.delete()
    }
  }

  "JsonFileIO" should {
    "save and load game field correctly" in {
      val gameField = GameField.init()
      val target = "test"

      sut.save(gameField, target)
      val loadedGameField = sut.load(target)
      loadedGameField shouldBe gameField
    }

    "load method should throw FileNotFoundException when file not found" in {
      val nonExistentFile = "nonExistentFile"

      try {
        sut.load(nonExistentFile)
        fail("Expected FileNotFoundException but no exception was thrown")
      } catch {
        case e: FileNotFoundException =>
          assert(e.getMessage.startsWith("File not found:"))
          assert(e.getMessage.contains(nonExistentFile))
      }
    }

    "return empty list if no targets available" in {
      sut.getTargets shouldBe List()
    }

    "return list of targets available" in {
      val gameField1 = GameField.init()
      val gameField2 = GameField.init()

      sut.save(gameField1, "test1")
      sut.save(gameField2, "test2")

      sut.getTargets should contain theSameElementsAs List("test1", "test2")
    }

    "create folder if it does not exist" in {
      val folder = new File("Persistence/saveGameJson")
      folder.exists() shouldBe false

      sut.save(GameField.init(), "test")

      folder.exists() shouldBe true
    }
  }
}

