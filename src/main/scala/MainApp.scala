import javafx.application.Application
import javafx.application.Application.launch
import javafx.fxml.FXMLLoader
import javafx.scene.{Parent, Scene}
import javafx.stage.Stage

class MainApp extends Application {
  override def start(primaryStage: Stage) = {
    Controller.init()
    val root: Parent = FXMLLoader.load(getClass.getResource("chat_room.fxml"))
    primaryStage.setTitle("p2p chat")
    primaryStage.setScene(new Scene(root))
    primaryStage.show()
  }
}

object MainApp {
  def main(args: Array[String]): Unit = {
    launch(classOf[MainApp], args: _*)
  }
}
