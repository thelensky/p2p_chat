import javafx.application.Application
import javafx.application.Application.launch
import javafx.fxml.FXMLLoader
import javafx.scene.{Parent, Scene}
import javafx.stage.Stage

class MainApp extends Application {
  override def start(primaryStage: Stage) = {
    val root: Parent = FXMLLoader.load(getClass.getResource("sample.fxml"))
    primaryStage.setTitle("Show data time")
    primaryStage.setScene(new Scene(root))
    primaryStage.show()
  }
}

object MainApp{
  // TODO init() where check: has it a host flag?
  // TODO must start GUI if all OK start cluster connection

  def main(args: Array[String]): Unit = {
    launch(classOf[MainApp], args: _*)
  }
}
