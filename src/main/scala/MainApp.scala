import javafx.application.Application
import javafx.application.Application.launch
import javafx.stage.Stage

class MainApp extends Application {
  override def start(primaryStage: Stage): Unit = {
    val view: View = Controller.chatRoom(primaryStage, "p2p chat")
    Model.mainChatView = view
    Controller.init(view)
  }
}

object MainApp {
  def main(args: Array[String]): Unit = {
    launch(classOf[MainApp], args: _*)
  }


}
