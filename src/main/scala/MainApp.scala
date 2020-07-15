import akka.actor.ActorSystem
import com.typesafe.config.{Config, ConfigFactory}
import javafx.application.Application.launch
import javafx.application.{Application, Platform}
import javafx.stage.Stage

import scala.concurrent.ExecutionContext

class MainApp extends Application {
  override def start(primaryStage: Stage): Unit = {
    val view: View = Controller.chatRoom(primaryStage, "p2p chat")
    Model.mainChatView = view
    Controller.enterForm(primaryStage)
    primaryStage.setOnCloseRequest(_ => {
      Model.system.terminate()
      Platform.exit()
      Model.system.whenTerminated.map(_ => {
        System.exit(0)
      })(ExecutionContext.global)
    })
  }
}

object MainApp {
  def main(args: Array[String]): Unit = {
    val conf: Config = ConfigFactory.load("cluster-akka.conf")
    val system = ActorSystem("ClusterSystem", conf)
    Controller.setSystem(system)
    launch(classOf[MainApp], args: _*)
  }
}