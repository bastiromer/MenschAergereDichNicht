package fileIO.fileIOComponents.impl

import model.modelComponents.GameField
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.io.{File, FileNotFoundException}
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.concurrent.ExecutionContext.Implicits.global


class JsonFileIOSpec extends AnyWordSpec with Matchers with BeforeAndAfterEach:
  val sut: JsonFileIO = JsonFileIO()

  override def beforeEach(): Unit = {
    val folder = new File("saveGameJson")
    if (folder.exists()) {
      folder.listFiles().foreach(_.delete())
      folder.delete()
    }
  }

  "JsonFileIO" should {
    "load method" should {
      "throw FileNotFoundException when file not found" in {
        val nonExistentFile = "nonExistentFile"

        val futureResult = sut.load(nonExistentFile)

        futureResult.failed.map {
          case e: FileNotFoundException =>
            assert(e.getMessage.startsWith("File not found:"))
            assert(e.getMessage.contains(nonExistentFile))
          case _ => fail("Expected FileNotFoundException but no exception was thrown")
        }
      }
      "return gameField when file found" in {
        val gameField = GameField.init()
        sut.save(gameField, "test")

        val field = Await.result(sut.load("test"), 5.seconds)
        field shouldBe gameField
      }
    }

    "return empty list if no targets available" in {
      sut.getTargets shouldBe List("saveGameJson\\test")
    }

    "return list of targets available" in {
      val gameField1 = GameField.init()
      val gameField2 = GameField.init()

      sut.save(gameField1, "test1")
      sut.save(gameField2, "test2")

      sut.getTargets should contain theSameElementsAs List("saveGameJson\\test", "saveGameJson\\test1", "saveGameJson\\test2")
    }

    "create folder if it does not exist" in {
      val folder = new File("saveGameJson")

      sut.save(GameField.init(), "test")

      folder.exists() shouldBe true
    }
  }
